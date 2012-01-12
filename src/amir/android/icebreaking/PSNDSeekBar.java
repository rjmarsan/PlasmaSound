package amir.android.icebreaking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class PSNDSeekBar extends SeekBar {

	public PSNDSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
//	public boolean isInScrollingContainer() {
//		Log.d("PSNDSeekBar", "Overriding isInScrollingContainer");
//		return true;
//	}
	
	
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		// TODO Auto-generated method stub
//		super.onLayout(changed, left, top, right, bottom);
//		
//		
//		try {
//			Method m = View.class.getMethod("isInScrollingContainer", null);
//			Boolean is = (Boolean)m.invoke(this, null);
//			
//			Log.d("PSNDSeekBar", "isInScrollingContainer: "+is);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//    protected void attemptClaimDrag() {
//		Log.d("PSNDSeekBar", "attemptClaimDrag: ");
//        if (getParent() != null) {
//            getParent().requestDisallowInterceptTouchEvent(true);
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	boolean ret = super.onTouchEvent(event);
//		if (getParent() != null) {
//			getParent().requestDisallowInterceptTouchEvent(false);
//		}
//    	return ret;
//    }
//	
//	

}
