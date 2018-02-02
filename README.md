

# SlidingCheckLayout

[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/) 

SlidingCheckLayout是一个滑动选中RecyclerView中Item的布局，手指滑过Item时多项选中。

> 作者：竹尘居士
>
> 博客：http://www.cnblogs.com/homg/p/8405008.html



## 示例 
  ![image](https://github.com/homgwu/SlidingCheckLayout/raw/master/sliding.gif)

## 特性

- SlidingCheckLayout继承自FrameLayout，使用时把RecyclerView放在SlidingCheckLayout里层。

- 左右滑动时手指滑到某项即回调该项的Position，上下滑动时根据position，回调开始和结束的position。

- 长按进入滑选模式。


## 实现方式

- 在dispatchTouchEvent中检测长按和处理滑动的位置：

  ```java
      @Override
      public boolean dispatchTouchEvent(MotionEvent event) {
          if (!isSlidingEnable() || !isEnabled()) {
              return super.dispatchTouchEvent(event);
          }
          if (!isCanIntercept()) {
              return super.dispatchTouchEvent(event);
          }
          final int action = event.getActionMasked();
          switch (action) {
              case MotionEvent.ACTION_DOWN:
  //                Log.i(TAG, "dispatchTouchEvent ACTION_DOWN mStartingCheck:" + mStartingCheck);
                  mInitDownY = mLastY = event.getY();
                  mInitDownX = mLastX = event.getX();
                  checkForLongClick(0, mInitDownX, mInitDownY);
                  break;
              case MotionEvent.ACTION_UP:
              case MotionEvent.ACTION_CANCEL:
  //                Log.i(TAG, "dispatchTouchEvent ACTION_CANCEL||ACTION_UP mStartingCheck:" + mStartingCheck);
                  removeLongPressCallback();
                  mLastPosition = RecyclerView.NO_POSITION;
                  mIncrease = 0;
                  if (mStartingCheck) {
                      mStartingCheck = false;
                      return true;
                  }
                  break;
              case MotionEvent.ACTION_MOVE:
  //                Log.i(TAG, "dispatchTouchEvent ACTION_MOVE mStartingCheck:" + mStartingCheck);
                  float y = event.getY();
                  float x = event.getX();
                  final float yInitDiff = y - mInitDownY;
                  final float xInitDiff = x - mInitDownX;
                  mLastY = y;
                  mLastX = x;
                  if (!mStartingCheck && (Math.abs(yInitDiff) > mTouchSlop || Math.abs(xInitDiff) > mTouchSlop)) {
                      removeLongPressCallback();
                  }
                  if (mStartingCheck) {
                      checkSlidingPosition(x, y);
                      return true;
                  }
                  break;
          }
          boolean result = super.dispatchTouchEvent(event);
  //        Log.i(TAG, "dispatchTouchEvent super.dispatchTouchEvent result:" + result);
          return result;
      }
  ```

  - 为何要在dispatchTouchEvent中处理而不在onInterceptTouchEvent和onTouchEvent中处理呢，因为如果是一些复杂的界面，SlidingCheckLayout的某上层还有可以滑动的布局如RecyclerView,ViewPager，他们可能会在Down或Move你返回false，里层RecyclerView也返回false时截断事件(而你又不能直接都返回True，在没进入滑选模式时要保证里层的RecyclerView还可以响应点击等事件)，那么SlidingCheckLayout会收不到后续的事件，而dispatchTouchEvent方法可以在SlidingCheckLayout不截断事件的情况下每次被调用到(询问是否要截断或分发到里层)。
  - 为何要在ACTION_UP时返回true，因为如果不在up时返回true那么这个up事件可能会被里层的RecyclerView响应成点击事件而多次处理点击的这个item。

- 长按处理：

  检查长按和移除(长按的代码是从View长按源码中copy出来改改的)，

  ```java
     private void checkForLongClick(int delayOffset, float x, float y) {
          if (mPendingCheckForLongPress == null) {
              mPendingCheckForLongPress = new CheckForLongPress();
          }
          mPendingCheckForLongPress.setAnchor(x, y);
          mPendingCheckForLongPress.rememberPressedState();
          mHandler.postDelayed(mPendingCheckForLongPress,
                  sLongPressTime - delayOffset);
      }

      private void removeLongPressCallback() {
          if (mPendingCheckForLongPress != null) {
              mHandler.removeCallbacks(mPendingCheckForLongPress);
          }
      }
  ```

  进入长按：

  ```java
      private final class CheckForLongPress implements Runnable {
          private float mX;
          private float mY;
          private boolean mOriginalPressedState;

          @Override
          public void run() {
              if ((mOriginalPressedState == isPressed()) && (mLastPosition = checkDownPosition(mX, mY)) != RecyclerView.NO_POSITION) {
                  if (mOnSlidingPositionListener != null) {
                      mOnSlidingPositionListener.onSlidingPosition(mLastPosition);
                  }
                  requestDisallowInterceptTouchEvent(true);
                  mStartingCheck = true;
              }
          }

          public void setAnchor(float x, float y) {
              mX = x;
              mY = y;
          }

          public void rememberPressedState() {
              mOriginalPressedState = isPressed();
          }
      }
  ```

- 检查滑过的是哪个item，并回调position

  ```java
     private void checkSlidingPosition(float x, float y) {
          View childViewUnder = mTargetRv.findChildViewUnder(x, y);
          if (mOnSlidingPositionListener == null || childViewUnder == null) return;

          int currentPosition = mTargetRv.getChildAdapterPosition(childViewUnder);
  //        Log.w(TAG, "checkSlidingPosition currentPosition:" + currentPosition + ",mLastPosition:" + mLastPosition);

          if (currentPosition == mLastPosition || currentPosition == RecyclerView.NO_POSITION) return;

          if (mLastPosition != RecyclerView.NO_POSITION && Math.abs(currentPosition - mLastPosition) > 1) {
              if (mLastPosition > currentPosition) {
                  mOnSlidingPositionListener.onSlidingRangePosition(currentPosition, mIncrease > 0 ? mLastPosition : mLastPosition - 1);
              } else {
                  mOnSlidingPositionListener.onSlidingRangePosition(mIncrease < 0 ? mLastPosition : mLastPosition + 1, currentPosition);
              }
          } else {
              if ((mIncrease > 0 && mLastPosition > currentPosition) || (mIncrease < 0 && currentPosition > mLastPosition)) {
                  mOnSlidingPositionListener.onSlidingPosition(mLastPosition);
              }
              mOnSlidingPositionListener.onSlidingPosition(currentPosition);
          }
          mIncrease = currentPosition > mLastPosition ? 1 : -1;
          mLastPosition = currentPosition;
      }
  ```

  通过RecyclerView的findChildViewUnder方法用坐标找到对应的子View，再getChildAdapterPosition就可以得到子View的位置了。

## 使用方法

1. 布局:

   ```xml
   <?xml version="1.0" encoding="utf-8"?>
       <com.homg.scl.SlidingCheckLayout
           android:id="@+id/scl"
           android:layout_width="0dp"
           android:layout_height="0dp"
           app:layout_constraintBottom_toBottomOf="parent"
           app:layout_constraintLeft_toLeftOf="parent"
           app:layout_constraintRight_toRightOf="parent"
           app:layout_constraintTop_toBottomOf="@id/reminder_tv">

           <android.support.v7.widget.RecyclerView
               android:id="@+id/rv"
               android:layout_width="match_parent"
               android:layout_height="match_parent" />
       </com.homg.scl.SlidingCheckLayout>
   ```

2. 设置Listener:

   ```java
    mSlidingCheckLayout.setOnSlidingPositionListener(this);

       @Override
       public void onSlidingPosition(int position) {
           MainEntity entity = mMainRvAdapter.getEntityByPosition(position);
           entity.setSelect(!entity.isSelect());
           mMainRvAdapter.notifyItemChanged(position);
       }

       @Override
       public void onSlidingRangePosition(int startPosition, int endPosition) {
           for (int i = startPosition; i <= endPosition; i++) {
               MainEntity entity = mMainRvAdapter.getEntityByPosition(i);
               entity.setSelect(!entity.isSelect());
           }
           mMainRvAdapter.notifyItemRangeChanged(startPosition, endPosition - startPosition + 1);
       }
   ```

