### 简介

本库可实现文本自由复制，样式统一、可定制行高。


### 接入说明

1. 项目根目录的build.gradle文件添加maven库

	```
	buildscript {
		repositories {
			google()
			jcenter()
		}
		dependencies {
			classpath "com.android.tools.build:gradle:4.1.1"
		}
	}
	allprojects {
		repositories {
			google()
			jcenter()
			maven { url "https://jitpack.io" }
		}
	}
	task clean(type: Delete) {
		delete rootProject.buildDir
	}
	```
	
2. 引用库

	```
	implementation 'com.github.wkjack:selectText:1.1.8'
	```

3. 使用
	
	1. 在对应控件处使用
	
		* 带文本选择的操作
			
			```
			SelectTextHelper mSelectableTextHelper = new SelectTextHelper.Builder(文本控件)
						.setSelectedColor(context.getResources().getColor(R.color.selected_blue))
						.setCursorHandleSizeInDp(20)
						.setCursorHandleColor(context.getResources().getColor(R.color.cursor_handle_color))
						.build();
			mSelectableTextHelper.setSelectOptionListener(new DefOnSelectOptionListener(mSelectableTextHelper) {
					@Override
					public List<SelectOption> calculateSelectInfo(@NonNull SelectionInfo selectionInfo, String textContent) {
						List<SelectOption> optionList = super.calculateSelectInfo(selectionInfo, textContent);
						optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
						optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
						return optionList;
					}
					
					@Override
					public void onSelectOption(@NonNull SelectionInfo selectionInfo, SelectOption option) {
						super.onSelectOption(selectionInfo, option);
						if (option.getType() == SelectOption.TYPE_CUSTOM) {
							Log.e("自定义选项：", option.toString());
						}
					}
			});
			```
	
			* setSelectedColor：设置文字选中背景色
			* setCursorHandleSizeInDp：游标大小
			* setCursorHandleColor：游标色值
			* setSelectOptionListener：选择回调监听

		* 不含文本选择的操作

			```
			SelectHelper mSelectHelper = new SelectHelper.Builder(文本控件).build();
			mSelectHelper.selectHelper.setSelectListener(new OnSelectListener() {
				@Override
				public List<SelectOption> calculateSelectInfo() {
					//自定义操作选项
					return null;
				}
			
				@Override
				public void onSelectOption(SelectOption selectOption) {}
			});
			```
	2. activity添加手势处理（解决点击空白区域关闭弹框）
	
		```
		public class ListActivity extends AppCompatActivity {
			
    		private GestureDetector gestureDetector;
    		
    		@Override
    		protected void onCreate(Bundle savedInstanceState) {
    			super.onCreate(savedInstanceState);
    			setContentView(R.layout.activity_list);
    			
    			...
    			
    			gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
    				@Override
    				public boolean onSingleTapUp(MotionEvent e) {
    					LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
    					if (lastSelect != null) {
    						lastSelect.clearOperate();
    						LastSelectManager.getInstance().setLastSelect(null);
    						return true;
    					}
    					return false;
					}
					
					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
						LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
						if (lastSelect != null) {
							if (lastSelect.isOnTouchDown()) {
								lastSelect.onScroll();
							} else {
								lastSelect.onScrollFromOther();
								LastSelectManager.getInstance().setLastSelect(null);
							}
						}
						return super.onScroll(e1, e2, distanceX, distanceY);
					}
					
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
						LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
						if (lastSelect != null) {
							if (lastSelect.isOnTouchDown()) {
								lastSelect.onFling();
							} else {
								lastSelect.onScrollFromOther();
								LastSelectManager.getInstance().setLastSelect(null);
							}
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
					
					@Override
					public boolean onDown(MotionEvent e) {
						LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
						if (lastSelect != null) {
							lastSelect.onTouchDownOutside(e);
						}
						return super.onDown(e);
					}
				});
			}
			
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
			
			@Override
			public boolean dispatchTouchEvent(MotionEvent event) {
				super.dispatchTouchEvent(event);
				return gestureDetector.onTouchEvent(event);
			}
		}
		```
	
	
	
### 参考资料

1. [Android自定义选择复制功能](https://juejin.cn/post/6844903569682857992)
2. [参考库](https://github.com/zhouray/SelectableTextView)
