package com.themoneywallet.dashboardservice.utilities;

import lombok.Data;

@Data
public class Pair<First, Second> {

    private final First first;
    private final Second second;
}
