package myapp.your_flashcards.Flashcard;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

/**
 * Created by Ronald on 2017/09/11.
 */

public class BoldTextTransformation {
    public BoldTextTransformation() {

    }

    public SpannableStringBuilder makeBold(String textToMakeBold, String regularText) {
        SpannableStringBuilder boldString = new SpannableStringBuilder(textToMakeBold + regularText);
        boldString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, textToMakeBold.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return boldString;
    }
}
