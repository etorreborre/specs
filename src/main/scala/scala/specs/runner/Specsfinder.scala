package scala.specs.runner

import scala.io.FileSystem
import java.util.regex._
import scala.collection.mutable.Queue

/**
 * This trait browses files on a given path and returns what can be matching specification class names 
 */  
trait SpecsFinder extends FileSystem {

   /** 
    * returns specification names by scanning files and trying to find specifications declarations
    * <code>path</code> is a path to a directory containing scala files (it can be a glob: i.e. "dir/**/*spec.scala")
    * <code>pattern</code> is a regular expression which is supposed to match an object name extending a Specification
    */
   def specificationNames(path: String, pattern: String) : List[String] = {
    var result = new Queue[String]
    filePaths(path).foreach { collectSpecifications(result, _, pattern) }
    result.toList
  }
  
  /**
   * adds possible specification class names found in the file <code>filePath</code>
   * <code>path</code> is a path to a directory containing scala files (it can be a glob: i.e. "dir/**/*spec.scala")
   * <code>pattern</code> is a regular expression which is supposed to match an object name extending a Specification
   * The specification pattern is: "\\s*object\\s*(" + pattern + ")\\s*extends\\s*.*Spec.*\\s*\\{"
   * This may be later extended to support other arbitrary patterns 
   */
  def collectSpecifications(result: Queue[String], filePath: String, pattern: String): Unit = {
    if (!filePath.endsWith(".scala")) return    
    val specPattern = "\\s*object\\s*(" + pattern + ")\\s*extends\\s*.*Spec.*\\s*\\{"
    val m = Pattern.compile(specPattern).matcher(readFile(filePath))
    while (m.find) {
      result += ((packageName(filePath) + "." + m.group(1).trim) + "$")
    }
  }
  
  /** returns the package declaration at the beginning of a file */
  def packageName(path: String) = {
    val pattern = "\\s*package\\s*(.+)\\s*"
    val m = Pattern.compile(pattern).matcher(readFile(path))
    if (!m.find) "" else m.group(1).replace(";", "").trim
  }
}
