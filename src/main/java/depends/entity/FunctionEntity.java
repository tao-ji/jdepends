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

package depends.entity;

import depends.relations.Inferer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FunctionEntity extends ContainerEntity {
	private List<String> returnTypeIdentifiers = new ArrayList<>();
	Collection<depends.entity.VarEntity> parameters;
    Collection<String> throwTypesIdentifiers = new ArrayList<>(); 
	private Collection<depends.entity.TypeEntity> returnTypes = new ArrayList<>();
	private Collection<depends.entity.TypeEntity> throwTypes = new ArrayList<>();
    public FunctionEntity(String simpleName, Entity parent, Integer id, String returnType) {
		super(simpleName, parent,id);
		this.returnTypes = new ArrayList<>();
		returnTypeIdentifiers = new ArrayList<>();
		this.parameters = new ArrayList<>();
		throwTypesIdentifiers = new ArrayList<>();
		addReturnType(returnType);
	}
	public Collection<depends.entity.TypeEntity> getReturnTypes() {
		return returnTypes;
	}
	
	@Override
	public depends.entity.TypeEntity getType() {
		if (returnTypes.size()>0)
			return returnTypes.iterator().next();
		return null;
	}

	public void addReturnType(String returnType) {
		if (returnType==null) return;
		this.returnTypeIdentifiers.add(returnType);
	}
	
	public void addReturnType(depends.entity.TypeEntity returnType) {
		if (returnType==null) return;
		if (!this.returnTypeIdentifiers.contains(returnType.rawName)){
			this.returnTypeIdentifiers.add(returnType.rawName);
			this.returnTypes.add(returnType);
		}
	}

	public void addThrowTypes(List<String> throwedType) {
		throwTypesIdentifiers.addAll(throwedType);
	}
	
	@Override
	public void inferLocalLevelEntities(Inferer inferer) {
		for (depends.entity.VarEntity param:parameters) {
			param.fillCandidateTypes(inferer);
			param.inferLocalLevelEntities(inferer);
		}
		if (returnTypes.size()<returnTypeIdentifiers.size())
			returnTypes = identiferToTypes(inferer,this.returnTypeIdentifiers);
		if (throwTypes.size()<throwTypesIdentifiers.size())
			throwTypes = identiferToTypes(inferer,this.throwTypesIdentifiers);
		super.inferLocalLevelEntities(inferer);
		if (this.returnTypes.size()==0 && this.getLastExpressionType()!=null) {
			this.returnTypes.add(this.getLastExpressionType());
		}
	}
	public Collection<depends.entity.VarEntity> getParameters() {
		return parameters;
	}
	public Collection<TypeEntity> getThrowTypes() {
		return throwTypes;
	}
	@Override
	public depends.entity.VarEntity lookupVarInVisibleScope(String varName) {
		for (depends.entity.VarEntity param:parameters) {
			if (varName.equals(param.getRawName())) {
				return param;
			}
		}
		return super.lookupVarInVisibleScope(varName);
	}
	public void addParameter(depends.entity.VarEntity var) {
		this.parameters.add(var);
	}
	@Override
	public String getDisplayName() {
		depends.entity.FileEntity f = (depends.entity.FileEntity) this.getAncestorOfType(FileEntity.class);
		return f.getRawName()+"("+this.getQualifiedName()+")";
	}
	@Override
	public depends.entity.VarEntity lookupVarLocally(String varName) {
		for (VarEntity var:this.parameters) {
			if (var.getRawName().equals(varName))
				return var;
		}
		return super.lookupVarLocally(varName);
	}

	@Override
	public String getQualifiedName(){
    	String qualifiedMethodName = qualifiedName;
    	qualifiedMethodName += "(";
    	for(VarEntity varEntity: parameters){
    		qualifiedMethodName += (varEntity.getRawType()+",");
		}
    	if(parameters.size()>0){
			qualifiedMethodName = qualifiedMethodName.substring(0,qualifiedMethodName.length()-1);
		}
		qualifiedMethodName += ")";

    	return qualifiedMethodName;
	}
}
