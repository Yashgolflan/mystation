package com.stayprime.updatesystem;

import com.stayprime.updatesystem.access.AccessSystem;

public interface UpdateMethodFactory {
    public UpdateMethodInstance getUpdateMethodInstance(AccessSystem accessSystem);
}
