package cn.com.wangcw.pdfhelper;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF转Markdown工具类
 * 该工具类可以将扫描版PDF文件通过OCR技术转换为Markdown格式文本
 */
public class PdfToMarkdownConverter {

    // 保存datapath和language配置
    private String datapath;
    private String language;
    private int dpi = 300; // 默认DPI

    /**
     * 构造函数，初始化Tesseract OCR引擎
     */
    public PdfToMarkdownConverter() {
        // 注意：如果需要使用特定语言或自定义路径，请调用相应setter方法
    }

    /**
     * 设置TessData路径
     * @param datapath tessdata文件夹路径
     */
    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }

    /**
     * 设置识别语言
     * @param language 语言代码，如"chi_sim"(简体中文),"eng"(英文)等
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 设置渲染DPI
     * @param dpi 渲染DPI值，推荐值为300-600
     */
    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    /**
     * 将PDF文件转换为Markdown文本
     *
     * @param pdfPath       输入PDF文件路径
     * @param markdownPath  输出Markdown文件路径
     * @throws IOException       文件读写异常
     * @throws TesseractException OCR识别异常
     */
    public void convertPdfToMarkdown(String pdfPath, String markdownPath) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder markdownContent = new StringBuilder();

        try {
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                // 将PDF页面渲染为图像，使用更高DPI和更适合OCR的图像类型
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, dpi, ImageType.GRAY);

                // 在进行OCR之前，确保图像有效
                if (image == null) {
                    System.err.println("警告: 第" + (page + 1) + "页渲染失败，跳过该页");
                    continue;
                }

                // 确保图像至少有一些内容
                if (image.getWidth() <= 0 || image.getHeight() <= 0) {
                    System.err.println("警告: 第" + (page + 1) + "页图像尺寸无效，跳过该页");
                    continue;
                }

                String text = "";
                try {
                    // 使用Tesseract进行OCR识别
                    // 为每一页创建完全独立的Tesseract实例以避免内存访问错误
                    Tesseract tesseract = new Tesseract();
                    if (this.datapath != null) {
                        tesseract.setDatapath(this.datapath);
                    }
                    if (this.language != null && !this.language.isEmpty()) {
                        tesseract.setLanguage(this.language);
                    }
                    
                    // 设置OCR配置参数以提高识别准确率
                    tesseract.setPageSegMode(6); // 使用适合文档的页面分割模式
                    tesseract.setOcrEngineMode(1); // 使用LSTM OCR引擎
                    
                    // 在单独的线程中执行OCR以避免内存访问冲突
                    text = performOCRInThread(tesseract, image);
                } catch (TesseractException e) {
                    System.err.println("第" + (page + 1) + "页OCR识别出错: " + e.getMessage());
                    // 出错时使用空文本继续处理而不是中断整个过程
                    text = "";
                } catch (Exception e) {
                    System.err.println("第" + (page + 1) + "页发生未知错误: " + e.getMessage());
                    // 捕获所有其他异常，防止程序崩溃
                    text = "";
                }

                // 分析文本结构并转换为Markdown
                String processedText = analyzeAndConvertToMarkdown(text);
                markdownContent.append(processedText).append("\n\n");
            }

            // 写入Markdown文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(markdownPath))) {
                writer.write(markdownContent.toString());
            }
        } finally {
            document.close();
        }
    }

    /**
     * 在独立线程中执行OCR操作以避免内存访问问题
     * @param tesseract Tesseract实例
     * @param image 待识别图像
     * @return OCR识别结果
     * @throws TesseractException OCR识别异常
     */
    private String performOCRInThread(Tesseract tesseract, BufferedImage image) throws TesseractException {
        OCRWorker worker = new OCRWorker(tesseract, image);
        Thread thread = new Thread(worker);
        thread.start();
        
        try {
            thread.join(); // 等待线程完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TesseractException("OCR线程被中断", e);
        }
        
        // 检查是否有异常
        if (worker.getException() != null) {
            throw worker.getException();
        }
        
        return worker.getResult();
    }

    /**
     * OCR工作线程类
     */
    private static class OCRWorker implements Runnable {
        private final Tesseract tesseract;
        private final BufferedImage image;
        private String result;
        private TesseractException exception;

        public OCRWorker(Tesseract tesseract, BufferedImage image) {
            this.tesseract = tesseract;
            this.image = image;
        }

        @Override
        public void run() {
            try {
                this.result = tesseract.doOCR(image);
            } catch (TesseractException e) {
                this.exception = e;
            }
        }

        public String getResult() {
            return result;
        }

        public TesseractException getException() {
            return exception;
        }
    }

    /**
     * 分析文本结构并转换为Markdown格式
     *
     * @param text 原始OCR识别文本内容
     * @return 转换后的Markdown文本
     */
    private String analyzeAndConvertToMarkdown(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] lines = text.split("\\r?\\n");
        List<String> markdownLines = new ArrayList<>();

        // 定义模式匹配
        Pattern headerPattern = Pattern.compile("^.{1,50}$"); // 假设较短行为标题
        Pattern listItemPattern = Pattern.compile("^([0-9]+\\.\\s|\\*\\s|-\\s)(.*)"); // 列表项

        boolean inList = false;

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) {
                if (inList) {
                    inList = false;
                }
                markdownLines.add("");
                continue;
            }

            // 检查是否为列表项
            Matcher listMatcher = listItemPattern.matcher(line);
            if (listMatcher.matches()) {
                if (!inList) {
                    inList = true;
                }
                markdownLines.add(line);
                continue;
            }

            // 检查是否为标题
            Matcher headerMatcher = headerPattern.matcher(line);
            if (headerMatcher.matches() && line.length() < 50) {
                // 简单判断标题级别
                if (line.equals(line.toUpperCase()) && line.length() < 30) {
                    markdownLines.add("## " + line); // 二级标题
                } else {
                    markdownLines.add("# " + line); // 一级标题
                }
                continue;
            }

            if (inList) {
                inList = false;
            }

            // 默认作为普通段落处理
            markdownLines.add(line);
        }

        return String.join("\n", markdownLines);
    }
}