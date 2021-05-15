package org.demo.production.planning.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.demo.rostering.domain.AbstractPersistable;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 1:22 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource extends AbstractPersistable {
    private String name;
    private SkillType skillType;

    public String getNameWithSkill() {
        return name + ("(" + skillType.name().charAt(0) + ")");
    }
}