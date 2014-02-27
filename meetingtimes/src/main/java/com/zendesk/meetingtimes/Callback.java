package com.zendesk.meetingtimes;

/**
 * Created by barry on 27/02/2014.
 */
public interface Callback<T> {

    void onResult(T result);

}
