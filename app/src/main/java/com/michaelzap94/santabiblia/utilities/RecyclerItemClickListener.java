package com.michaelzap94.santabiblia.utilities;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;

//We do this so we can override the SimpleOnItemTouchListener(captures any tap on screen) to handle long taps, 2taps, etc
public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";
    private final OnRecyclerClickListener rcListener;
    private final GestureDetectorCompat rvGestureDetector;

    public interface OnRecyclerClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener rcListener) {
        this.rcListener = rcListener;
        this.rvGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //THE MotionEvent will check which VIEW is UNDERNEATH IT.(Underneath the click) using the coordinates on the screen.
                // then the findChildViewUnder will return the childview that was found.
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

// FIND ALL CHILD VIEWS OF A VIEW
//                for(int index=0; index<((ViewGroup)childView).getChildCount(); ++index) {
//                    View nextChild = ((ViewGroup)childView).getChildAt(index);
//                    Log.d(TAG, "rlc onSingleTapUp: childView "+ nextChild);
//                    Log.d(TAG, "rlc onSingleTapUp: id: "+ nextChild.getId());
//                }
                TextView textView = (TextView) ((ViewGroup)childView).getChildAt(1);//this will be the verse textview

//                Log.d(TAG, "rlc onSingleTapUp: getMovementMethod " + textView.getMovementMethod());
//                Log.d(TAG, "rlc onSingleTapUp: textView.getSelectionStart() " + textView.getSelectionStart());
//                Log.d(TAG, "rlc onSingleTapUp: textView.getSelectionEnd()  " + textView.getSelectionEnd());

                if (textView.getSelectionStart() == -1 && textView.getSelectionEnd() == -1) {
                    // do your code here this will only call if its not a hyperlink
                    Log.d(TAG, "rlc DO CODE HERE. ");
                    if(childView != null && rcListener != null){
                        Log.d(TAG, "rlc onSingleTapUp: calling listener.onItemClick");
                        int positionOfTapRow = recyclerView.getChildAdapterPosition(childView);
                        rcListener.onItemClick(childView, positionOfTapRow);
                    }
                    return true;
                } else {
                    return super.onSingleTapUp(e);
                }
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //THE MotionEvent will check which VIEW is UNDERNEATH IT.(Underneath the click) using the coordinates on the screen.
                // then the findChildViewUnder will return the childview that was found.
                Log.d(TAG, "rlc onLongPress: ");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && rcListener != null){
                    Log.d(TAG, "rlc onLongPress: calling listener.onItemLongClick");
                    rcListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                } else {
                    super.onLongPress(e);
                }
            }

//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                Log.d(TAG, "onDoubleTap: ");
//                return super.onDoubleTap(e);
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                Log.d(TAG, "onDoubleTapEvent: ");
//                return super.onDoubleTapEvent(e);
//            }

            //Only called after hte detector is confident that the user's first tap is not followed by a second tap leading to a double-tap gesture.
//            @Override//USED if we need to handle both single and double tap; Androids waits to be SURE that it is a single tap.
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Log.d(TAG, "onSingleTapConfirmed: ");
//                return super.onSingleTapConfirmed(e);
//            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "rlc onInterceptTouchEvent: starts");
        //handle click events;//if you return true it means we have handled the click. so the default detector will not work;
        if(rvGestureDetector != null){
            //anything the gestureDetector handles do it here;
            boolean result = rvGestureDetector.onTouchEvent(e);
            Log.d(TAG, "rlc onInterceptTouchEvent rvGestureDetector: " + result);
            return result;
        } else {
            Log.d(TAG, "rlc onInterceptTouchEvent NO rvGestureDetector: ");
            return false;// therefore this will run: super.onInterceptTouchEvent(rv, e);
        }
        //return true;//super.onInterceptTouchEvent(rv, e);
    }
}
