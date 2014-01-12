[Japanese(MD)](README_ja.md)
[Japanese(HTML)](README_ja.html)


# Caffè Markdown

Markdown generate tool for Java and Scala.

[marked](https://github.com/chjj/marked) is used for conversion of a markdown file.


![Caffe Markdown: GUI](docs/images/CaffeMarkdown-GUI-s.png "Caffe Markdown: GUI")



## Install

```
$ git clone git://github.com/keisuken/caffemarkdown.git
```

* **Java 7 Runtime required**
* Copy from JRE jfxrt.jar to $CAFFEMARKDOWN_HOME/lib


## Usage



### Command line

Windows:

```
caffemd [options] files...
```

Mac or Linux:

```
caffemd.sh [options] files...
```

ex: (Windows)

```
caffemd -style default example.md
```

#### Usage

```
Usage: caffemd [options] file...
Options:
  -help               Display this information
  -version            Display version information
  -style <style_name> Set output style
  -wkhtmltopdf <wkhtmltopdf_path>
                      Set wkhtmltopdf execution path
  -pdf                Output PDF file
```


### GUI(Java FX)

Click for caffemdgui.bat or caffemdgui.sh .


#### Settings

##### Select wkhtmltopdf

1.Execution menu bar's "Settings" - "select wkhtmltopdf"
2.Select wkhtmltopdf(HTML to PDF converter) execution file

##### output to PDF

If otuput to PDF then check "output to PDF" menu item.


#### Open and generatet Markdown file

Execution menu bar' "File" - "Open and generate" menu item.



### Scala

```
import jp.cappuccino.tools.markdown.Markdown
...

// Create markdown generator.
val markdown = new Markdown

// Generate HTML.
val source = "# Header\n\nHello, Markdown tool!"
val style = Style.load(".", Style.Default)
val title = "Hello, Markdown!"
val html = markdown.generate(source, style, title)

// Print HTML.
println(html)
```

```
import jp.cappuccino.tools.markdown.Markdown
...

// Generate Markdown file.
val home = new File(".")
val styleName = Style.Default
val inpFile = new File("example.md")
val html = Markdown.generate(home, styleName, inpFile)

// Print HTML.
println(hrml)
```



## API Reference

See [Caffè Markdown](docs/api/index.html) .



## License

Copyright (C) 2014, NISHIMOTO Keisuke. (MIT License)

See:

* [LICENSE](LICENSE.txt)
* [marked LICENSE](marked-LICENSE.txt)
* [Scala LICENSE](Scala-LICENSE.txt)
