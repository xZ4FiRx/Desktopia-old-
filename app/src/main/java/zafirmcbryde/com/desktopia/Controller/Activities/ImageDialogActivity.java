package zafirmcbryde.com.desktopia.Controller.Activities;

import android.support.v4.app.Fragment;

import zafirmcbryde.com.desktopia.Controller.Fragments.ImageDialogFragment;

/**
 * Created by Zafir on 03/10/2017.
 */

public class ImageDialogActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return ImageDialogFragment.newInstance();
    }
}
