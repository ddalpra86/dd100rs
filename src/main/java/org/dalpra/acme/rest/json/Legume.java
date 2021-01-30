package org.dalpra.acme.rest.json;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@Cacheable
@RegisterForReflection
public class Legume extends PanacheEntity{

	@Column(length = 40, unique = true)
    public String name;
	@Column(length = 140)
    public String description;

    public Legume(){
    }

    public Legume(String name, String description) {
        this.name = name;
        this.description = description;
    }
}	