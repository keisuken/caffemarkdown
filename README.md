# Caffè Markdown

Markdown generate tool for Java and Scala.

Used by [marked](https://github.com/chjj/marked).



## Install

```
$ git clone git://github.com/keisuken/caffemarkdown/caffemarkdown.git
```

**Java 7 Runtime required**



## 使い方



### コマンドライン

Windows:

```
caffemd [style_name] input
```

Mac or Linux:

```
caffemd.sh [style_name] input
```

ex: (Windows)

```
caffemd default example.md
```



### GUI(Java FX)

Click for caffemdgui.bat or caffemdgui.sh .



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
