package myapp.your_flashcards;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import myapp.your_flashcards.Subject.SubjectFragment;
import myapp.your_flashcards.Reminder.ReminderFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private String[] arrTitles;
    private int numFragments;

    public CustomPagerAdapter(FragmentManager fm, String... paramTitles) {
        super(fm);
        if ((paramTitles != null && paramTitles.length > 0)) {
            arrTitles = new String[paramTitles.length];
            this.numFragments = paramTitles.length;
            for (int i = 0; i < paramTitles.length; i++) {
                arrTitles[i] = paramTitles[i];
            }
        } else {
            this.arrTitles = new String[1];
            this.arrTitles[0] = "Default One";
            this.arrTitles[1] = "Default Two";
        }

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            return new SubjectFragment();
        } else if (position == 1) {
            return new ReminderFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return this.numFragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrTitles[position];
    }
}
