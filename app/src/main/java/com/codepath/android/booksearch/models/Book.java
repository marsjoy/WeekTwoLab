package com.codepath.android.booksearch.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Book {
    @SerializedName("open_library_id")
    String openLibraryId;

    @SerializedName("author")
    String author;

    @SerializedName("title")
    String title;

    @SerializedName("publish_date")
    String publishDate;

    @SerializedName("description")
    String description;

    public String getOpenLibraryId() {
        return openLibraryId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getDescription() {
        return description;
    }

    // Get book cover from covers API
    public String getCoverUrl() {
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
    }

    // Returns a Book given the expected JSON
    public static Book fromJson(JSONObject jsonObject) {
        Book book = new Book();
        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.has("cover_edition_key")) {
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            } else if(jsonObject.has("edition_key")) {
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }
            book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";
            book.author = getValue(jsonObject, "author_name");
            book.publishDate = getMaxPublishDate(getValue(jsonObject, "publish_date"));
            book.description = jsonObject.has("description") ? jsonObject.getString("description") : "";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }

    private static String getMaxPublishDate(String publishDates) {
        String[] datesArray = publishDates.split(";");
        return  datesArray[0];
    }

    // Return comma separated author list when there is more than one author
    private static String getValue(final JSONObject jsonObject, String jsonKey) {
        try {
            final JSONArray values = jsonObject.getJSONArray(jsonKey);
            int numValues = values.length();
            final String[] valueStrings = new String[numValues];
            for (int i = 0; i < numValues; ++i) {
                valueStrings[i] = values.getString(i);
            }
            return TextUtils.join("; ", valueStrings);
        } catch (JSONException e) {
            return "";
        }
    }


    // Decodes array of book json results into business model objects
    public static ArrayList<Book> fromJson(JSONArray jsonArray) {
        ArrayList<Book> books = new ArrayList<>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson;
            try {
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Book book = Book.fromJson(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }
}

