package com.michaelzap94.santabiblia.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;

public abstract class SwipeToDelete extends ItemTouchHelper.Callback {
        //        private boolean swipeBack = false;
        Context mContext;
        private Paint mClearPaint;
        private ColorDrawable mBackground;
        private int backgroundColor;
        private Drawable deleteDrawable;
        private int intrinsicWidth;
        private int intrinsicHeight;

        public SwipeToDelete(Context context) {
            mContext = context;
            mBackground = new ColorDrawable();
            backgroundColor = Color.parseColor("#b80f0a");
            mClearPaint = new Paint();
            mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
            intrinsicWidth = deleteDrawable.getIntrinsicWidth();
            intrinsicHeight = deleteDrawable.getIntrinsicHeight();


        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }
//  to be implemented
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//            Toast.makeText(ctx, "on Swiped ", Toast.LENGTH_SHORT).show();
//            //Remove swiped item from list and notify the RecyclerView
//            int position = viewHolder.getAdapterPosition();
//            list.remove(position);
//            rvAdapter.notifyDataSetChanged();
//        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT );//| ItemTouchHelper.RIGHT
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            boolean isCancelled = dX == 0 && !isCurrentlyActive;

            if (isCancelled) {
                clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            mBackground.setColor(backgroundColor);
            mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;


            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


        }

        private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
            c.drawRect(left, top, right, bottom, mClearPaint);

        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.7f;//We’ve set the Swipe threshold to 0.7. That means if the row is swiped less than 70%, the onSwipe method won’t be triggered.
        }

//        // SwipeController.java
//        @Override
//        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//            if (swipeBack) {
//                swipeBack = false;
//                return 0;
//            }
//            return super.convertToAbsoluteDirection(flags, layoutDirection);
//        }
//
//        // SwipeController.java
//        @Override
//        public void onChildDraw(Canvas c,
//                                RecyclerView recyclerView,
//                                RecyclerView.ViewHolder viewHolder,
//                                float dX, float dY,
//                                int actionState, boolean isCurrentlyActive) {
//
//            if (actionState == ACTION_STATE_SWIPE) {
//                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
//
//        private void setTouchListener(Canvas c,
//                                      RecyclerView recyclerView,
//                                      RecyclerView.ViewHolder viewHolder,
//                                      float dX, float dY,
//                                      int actionState, boolean isCurrentlyActive) {
//
//            recyclerView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
//                    return false;
//                }
//            });
//        }
}