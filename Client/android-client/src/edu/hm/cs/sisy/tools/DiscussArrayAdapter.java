package edu.hm.cs.sisy.tools;

import java.util.ArrayList;
import java.util.List;

import edu.hm.cs.sisy.chat.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscussArrayAdapter extends ArrayAdapter<OneComment> {

	private TextView comment;
	private List<OneComment> commentList = new ArrayList<OneComment>();
	private LinearLayout wrapper;

	@Override
	public void add(OneComment object) {
		commentList.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.commentList.size();
	}

	public OneComment getItem(int index) {
		return this.commentList.get(index);
	}

	@SuppressLint("RtlHardcoded")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_discuss, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		OneComment coment = getItem(position);

		comment = (TextView) row.findViewById(R.id.comment);

		String sourceString = "<b>" + coment.alias + "</b><br />" + coment.comment; 
		comment.setText(Html.fromHtml(sourceString));
		
		//TODO: above works? alternative: comment.setText(coment.comment + "\n" + coment.comment);

		comment.setBackgroundResource(coment.left ? R.drawable.bubble_you : R.drawable.bubble_me);
		wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}