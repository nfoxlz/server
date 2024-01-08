package com.compete.mis.repositories;

import com.compete.mis.models.Tenant;
import com.compete.mis.models.User;

public interface TenantRepository {
    Tenant getTenant(final long id);

    Tenant getTenantByCode(final String code);

    User getUser(final Tenant tenant, final String code);
}
