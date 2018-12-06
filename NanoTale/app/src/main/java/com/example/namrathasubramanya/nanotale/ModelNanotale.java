package com.example.namrathasubramanya.nanotale;

import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by namrathasubramanya on 4/28/18.
 */

public class ModelNanotale {
    /*
        Generates URI for Content Providers, as of now.
     */

    public static final String _TAG = "model_util";

    /**
     * Builds the base Uri and returns the builder.
     * Returns the builder since all uses of the Uri are built upon the BaseUri.
     * @return BaseUri of NanoTale
     */
    private static Uri.Builder buildBaseUri() {
        Uri.Builder builder = new Uri.Builder()
                .scheme("content")
                .authority(NanotaleData.AUTHORITY)
                .appendPath(NanotaleData.NanoTale.PATH);
        return builder;
    }

    public static Uri buildQueryAllUri() {
        return buildBaseUri().build();
    }

    /**
     *
     * @return Uri to insert a nanotale record.
     */
    public static Uri buildInsertUri() {
        Uri.Builder builder = buildBaseUri()
                .appendPath(NanotaleContent.INSERT);
        return builder.build();
    }

    /**
     *
     * @param _id unique identifier of the record to be deleted.
     * @return Uri to delete a nanotale record with _id.
     */
    public static Uri buildDeleteSingleUri(String _id) {
        Uri.Builder builder = buildBaseUri()
                .appendPath(NanotaleContent.DELETE)
                .appendPath(_id);
        return builder.build();
    }

    /**
     *
     * @param nanotaleIds Ids of the records to be deleted.
     * @return Uri to delete multiple records.
     */
    public static Uri buildDeleteManyUri(String[] nanotaleIds) {
        /*
            The IDs of the records to be deleted are passed a query parameter.
            The parameter value is a concatenated string of all the IDs in
            nanotaleIds, with the delimiter being a comma.
         */
        String paramValue = TextUtils.join(",", nanotaleIds);

        Uri.Builder builder = buildBaseUri()
                .appendPath(NanotaleContent.DELETE_MANY)
                .appendQueryParameter(NanotaleContent.DELETE_MANY_QUERY_PARAM,
                        paramValue);
        return builder.build();
    }

    public static Uri buildUpdateUri(int _id) {
        Uri.Builder builder = buildBaseUri()
                .appendPath(Integer.toString(_id));
        return builder.build();
    }

    public static Uri buildUriForSavedNT(int _id) {
        Uri.Builder builder = buildBaseUri()
                .appendPath(Integer.toString(_id));
        return builder.build();
    }
}
