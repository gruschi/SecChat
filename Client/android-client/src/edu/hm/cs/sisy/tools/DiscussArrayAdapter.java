package edu.hm.cs.sisy.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import edu.hm.cs.sisy.chat.R;
import edu.hm.cs.sisy.enums.SCSmiley;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
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
	private Context context;

	@Override
	public void add(OneComment object) {
		commentList.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
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

		OneComment oneComment = getItem(position);

		//
		comment = (TextView) row.findViewById(R.id.comment);
		
		comment.setText("");

		//print Alias
		comment.append(Html.fromHtml("<b>" + oneComment.alias + "</b><br />"));
		//print comment/text-message
		//comment.append(oneComment.comment);
		comment.append(getSmiledText(context, oneComment.comment));

		comment.setBackgroundResource(oneComment.left ? R.drawable.bubble_you : R.drawable.bubble_me);
		
		wrapper.setGravity(oneComment.left ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	// get chat-text and convert letter-smiley like ;-) to an image-smiley
	public static Spannable getSmiledText(Context context, String text) {
      SpannableStringBuilder builder = new SpannableStringBuilder(text);
      int index;
      for (index = 0; index < builder.length(); index++) {
        for (Entry<String, Integer> entry : SCSmiley.EMOCTIONS.entrySet()) {
          int length = entry.getKey().length();
          if (index + length > builder.length())
            continue;
          if (builder.subSequence(index, index + length).toString().equals(entry.getKey())) {
            builder.setSpan(new ImageSpan(context, entry.getValue()), index, index + length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index += length - 1;
            break;
          }
        }
      }
      return builder;
	}
}