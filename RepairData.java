package com.example.gearguardian;


import java.util.LinkedHashMap;
import java.util.Map;

/** Static mapping of repair → suggestive repair. */
public class RepairData {
    public static final Map<String,String> REPAIR_MAP = new LinkedHashMap<>();

    static {
        REPAIR_MAP.put("Oil change", "Replace oil filter");
        REPAIR_MAP.put("Brake pad replacement", "Check brake fluid");
        REPAIR_MAP.put("Tire rotation", "Inspect tire pressure");
        REPAIR_MAP.put("Tire replacement", "Perform wheel alignment");
        REPAIR_MAP.put("Spark plug replacement", "Spark plug wire replacement");
        REPAIR_MAP.put("Battery replacement", "Clean the battery terminals");
        REPAIR_MAP.put("Transmission fluid change", "Inspect transmission for leaks");
        REPAIR_MAP.put("Coolant flush", "Check radiator hoses");
        REPAIR_MAP.put("Suspension repair", "Inspect shock absorbers");
        REPAIR_MAP.put("Fuel pump repair", "Clean fuel injectors");
        REPAIR_MAP.put("Timing belt replacement", "Replace water pump");
        REPAIR_MAP.put("Exhaust system repair", "Check emission sensors");
        REPAIR_MAP.put("Air conditioning repair", "Recharge refrigerant levels");
        REPAIR_MAP.put("Wheel bearing replacement", "Inspect suspension and alignment");
        // …add any remaining mappings here for furture update
    }
}
