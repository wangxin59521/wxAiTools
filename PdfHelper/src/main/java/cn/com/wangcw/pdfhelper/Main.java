package cn.com.wangcw.pdfhelper;

import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;

/**
 * 主程序类，演示如何使用PdfToMarkdownConverter工具类
 */
public class Main {
    public static void main(String[] args) {
        // 获取命令行参数，确定转换类型
        String conversionType = "markdown"; // 默认为markdown
        if (args.length > 0) {
            conversionType = args[0].toLowerCase();
        }

        // 创建转换器实例
        if ("word".equals(conversionType)) {
            convertToWord();
        } else {
            convertToMarkdown();
            convertToWord();
        }
    }

    private static void convertToMarkdown() {
        // 创建PDF转Markdown转换器实例
        PdfToMarkdownConverter converter = new PdfToMarkdownConverter();
        
        // 注意：如果需要使用特定语言或自定义路径，请取消下面的注释并设置正确路径
        // converter.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        // converter.setLanguage("chi_sim+eng"); // 同时使用中文和英文识别

        converter.setDatapath("D:\\dev\\Tesseract-OCR/tessdata");
        converter.setLanguage("chi_sim+eng");
        converter.setDpi(600); // 提高DPI以获得更好的OCR效果
        // 检查输入文件是否存在
        String inputPdf = "C:\\Users\\wangx\\Desktop\\Y24-DBS-CHN-00408 Signed 北京递蓝科软件股份有限公司.pdf";
        File pdfFile = new File(inputPdf);
        if (!pdfFile.exists()) {
            System.err.println("错误: 找不到输入文件 " + inputPdf);
            System.err.println("请确保在项目根目录下放置一个名为 sample.pdf 的PDF文件");
            return;
        }
        
        try {
            // 将扫描版PDF转换为Markdown文件
            converter.convertPdfToMarkdown(inputPdf, "output.md");
            System.out.println("PDF已成功转换为Markdown！");
            System.out.println("请查看项目目录下的output.md文件");
        } catch (IOException e) {
            System.err.println("文件读写错误: " + e.getMessage());
            System.err.println("请确保您有权限访问输入和输出文件");
            e.printStackTrace();
        } catch (TesseractException e) {
            System.err.println("OCR识别错误: " + e.getMessage());
            System.err.println("请确保已正确安装Tesseract OCR引擎");
            System.err.println("如果已安装，请检查tessdata路径是否正确配置");
            e.printStackTrace();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("链接错误: " + e.getMessage());
            System.err.println("这通常是因为Tesseract库未正确安装或配置");
            System.err.println("请检查Tesseract是否已正确安装并添加到系统PATH中");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("转换过程中发生未知错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void convertToWord() {
        // 创建PDF转Word转换器实例
        PdfToWordConverter converter = new PdfToWordConverter();
        
        // 注意：如果需要使用特定语言或自定义路径，请取消下面的注释并设置正确路径
        // converter.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        // converter.setLanguage("chi_sim+eng"); // 同时使用中文和英文识别

        converter.setDatapath("D:\\dev\\Tesseract-OCR/tessdata");
        converter.setLanguage("chi_sim+eng");
        converter.setDpi(600); // 提高DPI以获得更好的OCR效果
        // 检查输入文件是否存在
        String inputPdf = "C:\\Users\\wangx\\Desktop\\Y24-DBS-CHN-00408 Signed 北京递蓝科软件股份有限公司.pdf";
        File pdfFile = new File(inputPdf);
        if (!pdfFile.exists()) {
            System.err.println("错误: 找不到输入文件 " + inputPdf);
            System.err.println("请确保在项目根目录下放置一个名为 sample.pdf 的PDF文件");
            return;
        }
        
        try {
            // 将扫描版PDF转换为Word文件
            converter.convertPdfToWord(inputPdf, "output.docx");
            System.out.println("PDF已成功转换为Word！");
            System.out.println("请查看项目目录下的output.docx文件");
        } catch (IOException e) {
            System.err.println("文件读写错误: " + e.getMessage());
            System.err.println("请确保您有权限访问输入和输出文件");
            e.printStackTrace();
        } catch (TesseractException e) {
            System.err.println("OCR识别错误: " + e.getMessage());
            System.err.println("请确保已正确安装Tesseract OCR引擎");
            System.err.println("如果已安装，请检查tessdata路径是否正确配置");
            e.printStackTrace();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("链接错误: " + e.getMessage());
            System.err.println("这通常是因为Tesseract库未正确安装或配置");
            System.err.println("请检查Tesseract是否已正确安装并添加到系统PATH中");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("转换过程中发生未知错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}