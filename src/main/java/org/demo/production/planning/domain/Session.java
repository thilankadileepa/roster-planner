package org.demo.production.planning.domain;

import lombok.Data;
import org.demo.rostering.domain.AbstractPersistable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 7:26 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Session extends AbstractPersistable {
    private List<Skill> skillList;
}