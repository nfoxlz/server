package com.compete.mis.services;

import com.compete.mis.models.Tenant;
import com.compete.mis.repositories.TenantRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository repository;

    /**
     * @param id
     * @return
     */
    @Override
    public Tenant getTenant(final long id) {
        return repository.getTenant(id);
    }
}
