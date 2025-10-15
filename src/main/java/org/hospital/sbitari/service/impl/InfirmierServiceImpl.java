package org.hospital.sbitari.service.impl;

import org.hospital.sbitari.dao.InfirmierDao;
import org.hospital.sbitari.dao.impl.InfirmierDaoImpl;
import org.hospital.sbitari.entity.Infirmier;
import org.hospital.sbitari.service.InfirmierService;

import java.util.List;
import java.util.Optional;
import java.util.Optional;

public class InfirmierServiceImpl implements InfirmierService {

    private final InfirmierDao infirmierDao = new InfirmierDaoImpl();

    @Override
    public Optional<Infirmier> findById(Long id) {
        return infirmierDao.findById(id);
    }

    @Override
    public List<Infirmier> findAll() {
        return infirmierDao.findAll();
    }

    @Override
    public Infirmier create(Infirmier entity) {
        return infirmierDao.save(entity);
    }

    @Override
    public Infirmier update(Infirmier entity) {
        return infirmierDao.update(entity);
    }

    @Override
    public void delete(Infirmier entity) {
        infirmierDao.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        infirmierDao.deleteById(id);
    }
}
