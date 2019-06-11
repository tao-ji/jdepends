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

public abstract class DecoratedEntity extends depends.entity.Entity {
	private Collection<String> typeParameters; // Generic type parameters like <T>, <String>, <? extends Object>
	private Collection<String> annotations = new ArrayList<>();
	private Collection<depends.entity.TypeEntity> resolvedTypeParameters = new ArrayList<>();
	private Collection<depends.entity.TypeEntity> resolvedAnnotations = new ArrayList<>();
	
	public DecoratedEntity(String rawName, Entity parent, Integer id) {
		super(rawName, parent, id);
		typeParameters = new ArrayList<>();
	}
	
	public void addTypeParameter(List<String> typeArguments) {
		typeParameters.addAll(typeArguments);
	}
	public void addAnnotation(String name) {
		this.annotations.add(name);
	}
	
	public void addTypeParameter(String typeName) {
		this.typeParameters.add(typeName);
	}
	
	/**
	 * For all data in the class, infer their types.
	 * Should be override in sub-classes 
	 */
	public void inferLocalLevelEntities(Inferer inferer) {
		resolvedTypeParameters = identiferToTypes(inferer, typeParameters);
		resolvedAnnotations = identiferToTypes(inferer, annotations);
	}
	
	public Collection<depends.entity.TypeEntity> getResolvedTypeParameters() {
		return resolvedTypeParameters;
	}


	public Collection<depends.entity.TypeEntity> getResolvedAnnotations() {
		return resolvedAnnotations;
	}

	public boolean isGenericTypeParameter(String rawType) {
		if (this.typeParameters.contains(rawType)) return true;
		if (this.getParent()==null || !(this.getParent() instanceof depends.entity.ContainerEntity))
			return false;
		return ((depends.entity.ContainerEntity)getParent()).isGenericTypeParameter(rawType);
	}	
	/**
	 * A common utility function used to transfer the identifiers 
	 * to types.
	 * @param inferer - the inferer object 
	 * @param identifiers - the identifiers will be translated
	 * @return The translated Types
	 */
	protected Collection<depends.entity.TypeEntity> identiferToTypes(Inferer inferer, Collection<String> identifiers) {
		ArrayList<depends.entity.TypeEntity> r = new ArrayList<>();
		for (String typeParameter : identifiers) {
			TypeEntity typeEntity = inferer.inferTypeFromName(this, typeParameter);
			if (typeEntity==null) {
				if (((ContainerEntity)getParent()).isGenericTypeParameter(typeParameter)) {
					typeEntity = Inferer.genericParameterType;
				}
			}
			if (typeEntity != null)
				r.add(typeEntity);
		}
		return r;
	}
}
