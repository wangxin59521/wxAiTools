# PDF转换工具类

本工具类可以将扫描版PDF文件通过OCR技术转换为Markdown格式文本或Word文档。

## 功能特性

1. **PDF内容提取**: 集成OCR技术(如Tesseract)以识别扫描PDF中的图像文字，准确提取文本内容。
2. **文本结构分析**: 分析提取的文本内容，识别标题层级、段落分隔、列表项等文档结构元素。
3. **多种格式转换**: 支持将PDF转换为以下格式：
    - Markdown格式：根据识别的结构元素，应用Markdown语法规则进行格式转换
    - Word格式(.docx)：生成带格式的Word文档
4. **工具类设计**: 提供简洁的API接口，支持指定输入PDF路径和输出文件路径，包含异常处理机制。

## 依赖库

本项目使用Maven管理依赖，主要依赖库包括：

1. Apache PDFBox - 用于处理PDF文件
2. Tesseract OCR (通过Tess4J) - 用于光学字符识别
3. Apache POI - 用于生成Word文档

## 环境配置

### 安装Tesseract OCR

在使用本工具前，需要先安装Tesseract OCR引擎：

1. **Windows**:
   - 访问 https://github.com/UB-Mannheim/tesseract/wiki 下载适合Windows的安装包
   - 运行安装程序，默认会安装到 `C:\Program Files\Tesseract-OCR\` 目录
   - 将安装目录添加到系统环境变量PATH中

2. **Linux (Ubuntu/Debian)**:
   ```
   sudo apt update
   sudo apt install tesseract-ocr
   sudo apt install libtesseract-dev
   ```

3. **macOS**:
   ```
   brew install tesseract
   ```

### 语言包安装

默认情况下，Tesseract只包含英语识别数据。如需识别其他语言，需要额外安装语言包：

1. **中文简体识别**:
   - 下载 `chi_sim.traineddata` 文件
   - 将文件放置在Tesseract安装目录的 `tessdata` 子目录中

2. **其他语言**:
   - 从 https://github.com/tesseract-ocr/tessdata 下载对应语言的数据文件
   - 将文件放置在 `tessdata` 目录中

## 常见问题及解决方案

### 1. java.lang.Error: Invalid memory access 错误

这个错误通常是由于以下原因造成的：

1. **Tesseract未正确安装** - 确保已按照上面的说明正确安装Tesseract OCR引擎
2. **环境变量未配置** - 确保Tesseract安装目录已添加到系统PATH环境变量中
3. **tessdata路径未设置** - 在代码中明确设置tessdata路径:
   ```java
   converter.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // Windows示例
   converter.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata"); // Linux示例
   ```
4. **缺少语言数据包** - 确保tessdata目录中包含所需的语言数据文件
5. **权限问题** - 确保Java进程有权限访问Tesseract安装目录和数据文件

### 2. TesseractException 错误

这类错误通常与OCR处理有关，可能是由于图像质量问题或配置错误导致。

## 使用方法

1. 克隆或下载本项目

2. 使用Maven构建项目:
   ```
   mvn clean install
   ```

3. 在代码中使用工具类:
   ```java
   // 创建转换器实例（转换为Markdown）
   PdfToMarkdownConverter converter = new PdfToMarkdownConverter();
   
   // 创建转换器实例（转换为Word）
   PdfToWordConverter converter = new PdfToWordConverter();
   
   // 如需指定语言或tessdata路径，可进行设置
   converter.setLanguage("chi_sim+eng"); // 同时使用中英文识别
   converter.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // 指定tessdata路径
   converter.setDpi(600); // 设置更高的DPI以提高识别准确率（默认300）
   
   // 执行转换
   converter.convertPdfToMarkdown("input.pdf", "output.md"); // 转换为Markdown
   converter.convertPdfToWord("input.pdf", "output.docx");   // 转换为Word
   ```

4. 运行示例程序:
   ```
   // 转换为Markdown格式
   mvn exec:java -Dexec.mainClass="cn.com.wangcw.pdfhelper.Main"
   
   // 转换为Word格式
   mvn exec:java -Dexec.mainClass="cn.com.wangcw.pdfhelper.Main" -Dexec.args="word"
   ```

## 提升OCR识别效果的方法

为了获得更好的OCR识别效果，可以尝试以下优化方法：

1. **提高图像质量**：
   - 增加渲染DPI值（推荐使用600 DPI）
   - 使用GRAY图像类型而不是RGB以减少干扰
   - 确保原始PDF扫描质量较高

2. **优化Tesseract配置**：
   - 设置合适的页面分割模式（Page Seg Mode）
   - 使用LSTM OCR引擎以提高识别准确率
   - 正确配置语言包

3. **改善识别环境**：
   - 确保安装了最新版本的Tesseract OCR引擎
   - 安装适用于目标语言的高质量语言包
   - 确保系统有足够的内存和处理能力

## API说明

### PdfToMarkdownConverter()

构造函数，初始化Tesseract OCR引擎。

### void setDatapath(String datapath)

设置tessdata文件夹路径。

参数:
- datapath: tessdata文件夹的完整路径

### void setLanguage(String language)

设置识别语言。

参数:
- language: 语言代码，如"chi_sim"(简体中文)、"eng"(英文)，多语言用"+"连接

### void setDpi(int dpi)

设置PDF渲染时的DPI值。

参数:
- dpi: 渲染DPI值，推荐范围300-600

### void convertPdfToMarkdown(String pdfPath, String markdownPath)

将PDF文件转换为Markdown文本。

参数:
- pdfPath: 输入PDF文件路径
- markdownPath: 输出Markdown文件路径

异常:
- IOException: 文件读写错误
- TesseractException: OCR识别错误

### PdfToWordConverter()

构造函数，初始化Tesseract OCR引擎。

### void setDatapath(String datapath)

设置tessdata文件夹路径。

参数:
- datapath: tessdata文件夹的完整路径

### void setLanguage(String language)

设置识别语言。

参数:
- language: 语言代码，如"chi_sim"(简体中文)、"eng"(英文)，多语言用"+"连接

### void setDpi(int dpi)

设置PDF渲染时的DPI值。

参数:
- dpi: 渲染DPI值，推荐范围300-600

### void convertPdfToWord(String pdfPath, String wordPath)

将PDF文件转换为Word文档。

参数:
- pdfPath: 输入PDF文件路径
- wordPath: 输出Word文件路径

异常:
- IOException: 文件读写错误
- TesseractException: OCR识别错误

## 注意事项

1. 确保已正确安装Tesseract OCR引擎并将其添加到系统PATH中
2. 对于非英文内容，需要安装相应的语言包
3. 扫描质量会影响OCR识别准确率，建议使用清晰度较高的扫描件
4. 复杂布局的文档可能无法完美识别结构
5. 适当增加DPI值可以提高识别准确率，但会增加处理时间和内存消耗