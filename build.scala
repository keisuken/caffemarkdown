/*
 * build.scala
 *   Markdown tool build script.
 * Copyright (C) 2014 NISHIMOTO Keisuke. All Rights Reserved.
 */


Project("Caffè Markdown Tool", ".", 'build)


/*====================================================================
  Directories.
====================================================================*/

Props(
  'lib -> "lib",
  'src -> "src",
  'classes -> "classes",
  'docs -> "docs",
  'api -> "${docs}/api"
)


/*====================================================================
  CLASSPATH.
====================================================================*/

Props(
  'scala_library_jar -> "${lib}/scala-library.jar",
  'scala_compiler_jar -> "${lib}/scala-compiler.jar",
  'javafx_jar -> "${lib}/jfxrt.jar",
  'target_jar -> "${lib}/caffe-markdown.jar"
)

val classPath = ClassPath(
  'scala_library_jar,
  'scala_compiler_jar,
  'javafx_jar,
  'classes
)


/*====================================================================
  Build rules.
====================================================================*/

Task('build) {task =>
  MakeDir('classes)
  Copy(
    "${src}/*", // */
    'classes,
    filter = FileFilter.includes(".js", ".fxml"))
  ScalaCompiler(
    'src,
    'classes,
    classpath = classPath,
    options = List("-deprecation")
  )
  Jar('classes, 'target_jar)
}


/*====================================================================
  Run rules.
====================================================================*/

Task('api) {task =>
  MakeDir('api)
  ScalaDoc(
    'src,
    'api,
    classpath = classPath,
    doctitle = "Caffè Markdown API",
    options = List("-feature", "-language:implicitConversions")
  )
}


/*====================================================================
  Clean up rules.
====================================================================*/

Task('clean) {task =>
  Delete('classes, true)
}
