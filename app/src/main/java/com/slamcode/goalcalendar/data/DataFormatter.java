package com.slamcode.goalcalendar.data;

public interface DataFormatter<DataBundle extends DataBundleAbstract> {

    String formatDataBundle(DataBundle dataBundle);
}
