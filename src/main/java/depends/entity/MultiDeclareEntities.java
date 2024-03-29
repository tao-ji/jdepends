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

/**
 * MultiDeclareEntity is a special container, which is used as a wrapper
 * of multi-declaration. for example, 
 * in C++, a function could be declared in different place with the same signature.
 */
public class MultiDeclareEntities extends depends.entity.ContainerEntity {
	List<ContainerEntity> entities = new ArrayList<>();
	public MultiDeclareEntities(depends.entity.Entity entity, int id ) {
		super(entity.getRawName(), entity.getParent(), id);
		add(entity);
	}

	@Override
	public void inferLocalLevelEntities(Inferer inferer) {
		for (depends.entity.Entity entity:entities) {
			entity.inferLocalLevelEntities(inferer);
		}
	}

	public void add(depends.entity.Entity entity) {
		entity.setMutliDeclare(this);
		if (entity instanceof depends.entity.ContainerEntity)
			entities.add((depends.entity.ContainerEntity)entity);
	}

	public List<ContainerEntity> getEntities() {
		return entities;
	}

	@Override
	public Collection<Entity> getChildren() {
		List<Entity> children = new ArrayList<>();
		for (depends.entity.Entity entity:entities) {
			children.addAll(entity.getChildren());
		}
		return children;
	}

	@Override
	public TypeEntity getType() {
		for (Entity entity:entities) {
			if(entity.getType()!=null);
				return entity.getType();
		}
		return null;
	}
}
