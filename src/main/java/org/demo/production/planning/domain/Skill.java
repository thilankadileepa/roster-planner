package org.demo.production.planning.domain;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 7:19 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Skill {
    private SkillType skillType;
    private int requiredCount;
}