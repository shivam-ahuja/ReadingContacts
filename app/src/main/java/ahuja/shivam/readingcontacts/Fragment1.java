package ahuja.shivam.readingcontacts;


import android.app.Activity;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
    public class Fragment1 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener{


    public static final String[] projection={ContactsContract.Contacts._ID,
                                            ContactsContract.Contacts.LOOKUP_KEY,
                                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                                            ContactsContract.Contacts.HAS_PHONE_NUMBER};
    public static final int Contact_column_id_index=0;
    public static final int contact_contact_lookupkey_index=1;
    public static final String selection=ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
                                         "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1";
    private String mCurrentSearch;


    private ListView myListView;

    private long selected_contact_id;
    private String selected_contact_lookupkey;
    private Uri selected_contact_uri;
    private EditText mSearchEt;
    private MyAdapter myadapter;


    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment1, container, false);
        myListView=(ListView)view.findViewById(R.id.mylistview);
        mSearchEt=(EditText)view.findViewById(R.id.search_text);
        getLoaderManager().initLoader(0,null,this);
        myadapter=new MyAdapter(getContext(),null,false);
        myListView.setAdapter(myadapter);
        myListView.setOnItemClickListener(this);
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mCurrentSearch=charSequence.toString();
                if(mCurrentSearch.isEmpty())
                {
                    mCurrentSearch=null;
                }
                getLoaderManager().restartLoader(0,null,Fragment1.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    if(mCurrentSearch==null)
    {
    return new CursorLoader(getContext(),
                            ContactsContract.Contacts.CONTENT_URI,
                            projection,selection,null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
    }
    return new CursorLoader(getContext(),
                            Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(mCurrentSearch)),
                            projection,selection,null, ContactsContract.Contacts.SORT_KEY_PRIMARY);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myadapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myadapter.swapCursor(null);
    }


    //on listview item click
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
      Cursor cursor=  ((MyAdapter)adapterView.getAdapter()).getCursor();
        cursor.moveToPosition(i);
       selected_contact_id= cursor.getLong(Contact_column_id_index);
        selected_contact_lookupkey=cursor.getString(contact_contact_lookupkey_index);
        selected_contact_uri=ContactsContract.Contacts.getLookupUri(selected_contact_id,selected_contact_lookupkey);
        //can perform any task on OnClick as we have contact uri of that paricular contact
    }
}
