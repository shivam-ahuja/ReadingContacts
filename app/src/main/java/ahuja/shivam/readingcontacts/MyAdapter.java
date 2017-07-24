package ahuja.shivam.readingcontacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 23/7/17.
 */

public class MyAdapter extends CursorAdapter {
    private final LayoutInflater lif;
    ;

    private long current_contact_id;
    private String current_contact_lookupkey;
    private Uri current_contact_uri;

    public MyAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        lif= LayoutInflater.from(context);

    }
    class Holder{
         TextView ContactNameTv;
         TextView FirstLetterTv;
         TextView ContactPhoneNoTv;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view= lif.inflate(R.layout.listview_item_layout,viewGroup,false);
        Holder holder=new Holder();
        holder.FirstLetterTv=(TextView)view.findViewById(R.id.contact_firstname_tv);
        holder.ContactNameTv=(TextView)view.findViewById(R.id.contact_name_tv);
        holder.ContactPhoneNoTv=(TextView)view.findViewById(R.id.contact_phone_no_tv);
        int mycolors[]=context.getResources().getIntArray(R.array.androidcolors);
        int random= mycolors[new Random().nextInt(mycolors.length-1)];
        holder.FirstLetterTv.setTextColor(random);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor!=null) {
            Holder holder=(Holder) view.getTag();
            String name = cursor.getString(2).trim();
            holder.ContactNameTv.setText(name);
            holder.FirstLetterTv.setText(String.valueOf(name.charAt(0)));
            current_contact_id = cursor.getLong(0);
                    current_contact_lookupkey = cursor.getString(1);
                    current_contact_uri = ContactsContract.Contacts.getLookupUri(current_contact_id, current_contact_lookupkey);
                    holder.ContactPhoneNoTv.setText(fetchNumberFromURI(current_contact_uri, context));

        }
        }


        //fetching conact nos for contracts
  private String fetchNumberFromURI(Uri uri, Context context) {
      Cursor cursor;  // Cursor object
      String mime;    // MIME type
      int dataIdx;    // Index of DATA1 column
      int mimeIdx;    // Index of MIMETYPE column
      int nameIdx;
      String phone=null;// Index of DISPLAY_NAME column
      String name = null;
      String sendphonelist="";
      // Get the name
      try {
          cursor = context.getContentResolver().query(uri,
                  new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                  null, null, null);
          if (cursor != null) {
              if (cursor.moveToFirst() == true) {
                  nameIdx = cursor.getColumnIndex(
                          ContactsContract.Contacts.DISPLAY_NAME);
                  name = cursor.getString(nameIdx);
                  String[] projection = {
                          ContactsContract.Data.DISPLAY_NAME,
                          ContactsContract.Contacts.Data.DATA1,
                          ContactsContract.Contacts.Data.MIMETYPE};
                  cursor = context.getContentResolver().query(
                          ContactsContract.Data.CONTENT_URI, projection,
                          ContactsContract.Data.DISPLAY_NAME + " = ?",
                          new String[]{name},
                          null);
                  if (cursor != null) {
                      if (cursor.moveToFirst() == true) {
                          // Get the indexes of the MIME type and data
                          mimeIdx = cursor.getColumnIndex(
                                  ContactsContract.Contacts.Data.MIMETYPE);
                          dataIdx = cursor.getColumnIndex(
                                  ContactsContract.Contacts.Data.DATA1);
                          do {
                              mime = cursor.getString(mimeIdx);
                              if (ContactsContract.CommonDataKinds.Phone
                                      .CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
                                  phone = cursor.getString(dataIdx);
                                  phone = phone.replaceAll("[^0-9]", "");
                                  phone = phone.substring(phone.length()-10);
                                 // Log.d("shivam",name+">>>>"+phone);
                                  sendphonelist=phone+","+sendphonelist;
                              }
                          } while (cursor.moveToNext());
                      }
                      cursor.close();
                  }
              }
              cursor.close();
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      if(sendphonelist.endsWith(",")) {
         sendphonelist= sendphonelist.substring(0, sendphonelist.length() - 1);
      }
          return sendphonelist;
  }
}
