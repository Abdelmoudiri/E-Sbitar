package org.hospital.sbitari.dao.impl;

import org.hospital.sbitari.dao.InfirmierDao;
import org.hospital.sbitari.dao.JpaGenericDao;
import org.hospital.sbitari.entity.Infirmier;

public class InfirmierDaoImpl extends JpaGenericDao<Infirmier, Long> implements InfirmierDao {
    public InfirmierDaoImpl() {
        super(Infirmier.class);
    }
}
