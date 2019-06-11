/*
MIT License

Copyright (c) 2018-2019 Gang ZHANG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package depends.extractor;

import depends.entity.repo.BuiltInType;
import depends.entity.repo.EntityRepo;
import depends.matrix.core.DependencyMatrix;
import depends.relations.ImportLookupStrategy;
import depends.relations.Inferer;
import depends.util.FileTraversal;
import depends.util.FileUtil;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import depends.generator.DependencyGenerator;

abstract public class AbstractLangProcessor {
	/**
	 * The name of the lang
	 * @return
	 */
	public abstract String supportedLanguage();
	/**
	 * The file suffixes in the lang
	 * @return
	 */
	public abstract String[] fileSuffixes();

	/**
	 * Strategy of how to lookup  types and entities in the lang.
	 * @return
	 */
	public abstract ImportLookupStrategy getImportLookupStrategy();
	/**
	 * The builtInType of the lang. 
	 * @return
	 */
	public abstract BuiltInType getBuiltInType();
	/**
	 * The language specific file parser
	 * @param fileFullPath
	 * @return
	 */
    protected abstract FileParser createFileParser(String fileFullPath);

	public Inferer inferer;
	protected EntityRepo entityRepo;
	DependencyMatrix dependencyMatrix;
	private String inputSrcPath;
	public String[] includeDirs;
//	private DependencyGenerator dependencyGenerator;

	public AbstractLangProcessor(boolean eagerExpressionResolve) {
		entityRepo = new EntityRepo();

		inferer = new Inferer(entityRepo,getImportLookupStrategy(),getBuiltInType(),eagerExpressionResolve);
	}
	
    /**
     * The process steps of build dependencies.
     * Step 1: parse all files, add entities and expression into repositories
     * Step 2: resolve bindings of files (if not resolved yet)
     * Step 3: identify dependencies 
     * @param includeDir 
     * @param inputDir 
     */
	public void buildDependencies(String inputDir, String[] includeDir) {
		this.inputSrcPath = inputDir;
		this.includeDirs = includeDir;
        parseAllFiles();
        resolveBindings();
//        identifyDependencies();
	}


	/**
	 * 
	 * @return unsolved bindings
 	 */
    private void resolveBindings() {
        Set<String> unsolved = inferer.resolveAllBindings();
        if (unsolved.size()>0)
        	System.err.println("The following items are unsolved." + unsolved);
    }
    
//    private void identifyDependencies(){
//		System.out.println("dependencie data generating...");
//        dependencyMatrix  = dependencyGenerator.build(entityRepo);
//        dependencyMatrix = new OrderedMatrixGenerator(dependencyMatrix).build();
//        System.out.println("dependencie data generating done successfully...");
//    }

    private final void parseAllFiles() {
    	FileTraversal fileTransversal = new FileTraversal(new FileTraversal.IFileVisitor(){
			@Override
			public void visit(File file) {
				String fileFullPath = file.getAbsolutePath();
				fileFullPath = FileUtil.uniqFilePath(fileFullPath);
	            FileParser fileParser = createFileParser(fileFullPath);
	            try {
//	                System.out.println("parsing " + fileFullPath
//	                		+ "...");
	                fileParser.parse();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }	
			}
    		
    	});
    	fileTransversal.extensionFilter(this.fileSuffixes());
		fileTransversal.travers(this.inputSrcPath);

	}
    

	public List<String> includePaths() {
		ArrayList<String> r = new ArrayList<String>();
		for (String path:includeDirs) {
			if (FileUtils.fileExists(path)) {
				if (!r.contains(path))
					r.add(path);
			}
			path = this.inputSrcPath +File.separator+path;
			if (FileUtils.fileExists(path)) {
				if (!r.contains(path))
					r.add(path);
			}
		}
		return r;
	}
	
	public DependencyMatrix getDependencies() {
		return dependencyMatrix;
	}
	public EntityRepo getEntityRepo() {
		return this.entityRepo;
	}

//	public void setDependencyGenerator(DependencyGenerator dependencyGenerator) {
//		this.dependencyGenerator = dependencyGenerator;
//	}
	public abstract List<String> supportedRelations();

}
