import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

import java.nio.file.*


def packageName=this.args[1]
def projectName=this.args[0]

println "Generating android project:$packageName / $projectName"
dstFolder=new File("test")
dstFolder.mkdirs()
srcFolder=new File("template/android")
def binding=[
	"packageName":packageName,
	"projectName":projectName,
	"packageNameAsDir":packageName.replace('.','/')
	];
deepCreate(srcFolder,dstFolder,binding)

void deepCreate(srcFolder,dstFolder,binding)
{
	def engine = new SimpleTemplateEngine()	
	for(file in srcFolder.listFiles())
	{	
		def template=engine.createTemplate(file.name).make(binding)
		def dest=dstFolder.absolutePath+"/"+template.toString();
		println "\tCopying:\n\t\t$file.name to $dest"
		if(file.isDirectory())
		{
			
			def newSrcFolder=file
			
			new File(dest).mkdirs();
			deepCreate(newSrcFolder,new File(dest),binding)
		}
		else
		{
			if(dest.endsWith(".template"))
			{
				def newFile=dest.replace(".template", "")
				def fileContents=engine.createTemplate(file).make(binding)
				new File(newFile).write(fileContents.toString());
			}	
			else
			{	
				def src=file.absolutePath
				Files.copy(Paths.get(src),Paths.get(dest),StandardCopyOption.REPLACE_EXISTING);				
			}
		}
	}
}

