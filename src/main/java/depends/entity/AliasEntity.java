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

public class AliasEntity extends depends.entity.Entity {
	private depends.entity.Entity referToEntity = new EmptyTypeEntity();
	private String originName;

	public AliasEntity(String simpleName, depends.entity.Entity parent, Integer id, String originTypeName) {
		super(simpleName, parent, id);
		this.originName = originTypeName;
	}

	public void inferLocalLevelEntities(Inferer inferer) {
		depends.entity.Entity entity = inferer.resolveName(this, originName, true);
		while(entity instanceof AliasEntity) {
			AliasEntity aliasEntity = (AliasEntity)entity;
			entity = inferer.resolveName(aliasEntity, aliasEntity.originName,true);
			if (entity==null) break;
			if (entity.equals(this)) {
				entity = null;
				break;
			}
		}
		if (entity != null)
			referToEntity = entity;
	}

	public Collection<depends.entity.TypeEntity> getResolvedTypeParameters() {
		if (!(referToEntity instanceof DecoratedEntity))
			return new ArrayList<>();
		DecoratedEntity origin = (DecoratedEntity) referToEntity;
		return origin.getResolvedTypeParameters();
	}

	public Collection<depends.entity.TypeEntity> getResolvedAnnotations() {
		if (!(referToEntity instanceof DecoratedEntity))
			return new ArrayList<>();
		DecoratedEntity origin = (DecoratedEntity) referToEntity;
		return origin.getResolvedAnnotations();
	}

	public ArrayList<depends.entity.VarEntity> getVars() {
		if (!(referToEntity instanceof ContainerEntity))
			return new ArrayList<>();
		ContainerEntity origin = (ContainerEntity) referToEntity;
		return origin.getVars();
	}

	public ArrayList<FunctionEntity> getFunctions() {
		if (!(referToEntity instanceof ContainerEntity))
			return new ArrayList<>();
		ContainerEntity origin = (ContainerEntity) referToEntity;
		return origin.getFunctions();
	}

	protected FunctionEntity lookupFunctionLocally(String functionName) {
		if (!(referToEntity instanceof ContainerEntity))
			return null;
		ContainerEntity origin = (ContainerEntity) referToEntity;
		return origin.lookupFunctionLocally(functionName);
	}

	public FunctionEntity lookupFunctionInVisibleScope(String functionName) {
		if (!(referToEntity instanceof ContainerEntity))
			return null;
		ContainerEntity origin = (ContainerEntity) referToEntity;
		return origin.lookupFunctionInVisibleScope(functionName);
	}

	public depends.entity.VarEntity lookupVarsInVisibleScope(String varName) {
		if (!(referToEntity instanceof ContainerEntity))
			return null;
		ContainerEntity origin = (ContainerEntity) referToEntity;
		return origin.lookupVarInVisibleScope(varName);
	}

	public Collection<ContainerEntity> getResolvedMixins() {
		if (!(referToEntity instanceof ContainerEntity))
			return new ArrayList<>();
		ContainerEntity origin = (ContainerEntity) referToEntity;
		return origin.getResolvedMixins();
	}

	public Collection<depends.entity.TypeEntity> getInheritedTypes() {
		if (referToEntity instanceof depends.entity.TypeEntity)
			return ((depends.entity.TypeEntity) referToEntity).getInheritedTypes();
		return new ArrayList<>();
	}

	public Collection<depends.entity.TypeEntity> getImplementedTypes() {
		if (referToEntity instanceof depends.entity.TypeEntity)
			return ((depends.entity.TypeEntity) referToEntity).getImplementedTypes();
		return new ArrayList<>();
	}

	public depends.entity.TypeEntity getInheritedType() {
		if (referToEntity instanceof depends.entity.TypeEntity)
			return ((depends.entity.TypeEntity) referToEntity).getInheritedType();
		return null;
	}

	public Collection<depends.entity.TypeEntity> getReturnTypes() {
		if (!(referToEntity instanceof FunctionEntity))
			return new ArrayList<>();
		FunctionEntity origin = (FunctionEntity) referToEntity;
		return origin.getReturnTypes();
	}

	public depends.entity.TypeEntity getType() {
		return referToEntity.getType();
	}

	public Collection<VarEntity> getParameters() {
		if (!(referToEntity instanceof FunctionEntity))
			return new ArrayList<>();
		FunctionEntity origin = (FunctionEntity) referToEntity;
		return origin.getParameters();
	}

	public Collection<TypeEntity> getThrowTypes() {
		if (!(referToEntity instanceof FunctionEntity))
			return new ArrayList<>();
		FunctionEntity origin = (FunctionEntity) referToEntity;
		return origin.getThrowTypes();
	}

	public Entity getOriginType() {
		return referToEntity;
	}
	

}
