package it.poliba.sisinflab.physicalweb;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.physical_web.physicalweb.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

/**
 * Created by giorgio on 01/04/15.
 */
public class UserData implements Serializable {

    static final long serialVersionUID = 42L;
    private static final String USER_DATA_PATH = "userData.bin";

    private ArrayList<String> history;
    private ArrayList<String> bookmarks;
    private ArrayList<String> spam;


    UserData() {
        history = new ArrayList<String>();
        bookmarks = new ArrayList<String>();
        spam = new ArrayList<String>();
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }

    public ArrayList<String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(ArrayList<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public ArrayList<String> getSpam() {
        return spam;
    }

    public void setSpam(ArrayList<String> spam) {
        this.spam = spam;
    }

    public boolean addBookmark(String url, Context context) {
        int index = spam.indexOf(url);
        if (index != -1) {
            Toast.makeText(context, R.string.is_spam, Toast.LENGTH_SHORT).show();
        } else {
            if (!bookmarks.contains(url)) {
                bookmarks.add(url);
                save(context);
                return true;
            }
        }
        return false;
    }

    public boolean markSpam(String url, Context context) {
        int index = bookmarks.indexOf(url);
        if (index != -1) {
            Toast.makeText(context, R.string.is_bookmark, Toast.LENGTH_SHORT).show();
        } else {
            if (!spam.contains(url)) {
                spam.add(url);
                save(context);
                return true;
            }
        }
        return false;
    }

    public static UserData load(Context context) {
        try {
            //use buffering
            InputStream file = context.openFileInput(USER_DATA_PATH);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //deserialize the List
            UserData d = (UserData) input.readObject();
            Log.i("PhysicalWeb", "User data loaded");
            return d;


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.i("PhysicalWeb", "No user data");
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new UserData();
    }

    public void save(Context context) {
        try {
            OutputStream file = context.openFileOutput(USER_DATA_PATH, Context.MODE_PRIVATE);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            try {
                output.writeObject(this);
            } finally {
                output.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
