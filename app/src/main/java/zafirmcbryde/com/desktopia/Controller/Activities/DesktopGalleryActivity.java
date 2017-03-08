package zafirmcbryde.com.desktopia.Controller.Activities;

import android.support.v4.app.Fragment;

import zafirmcbryde.com.desktopia.Controller.DesktopGalleryFragment;

public class DesktopGalleryActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return DesktopGalleryFragment.newInstance();
    }
}
