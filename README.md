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
	implementation 'com.github.wkjack:selectText:1.2.0'
	```

3. 使用
	
	1. 在对应控件处使用
	
		* 带文本选择的操作
			
			```
			SelectTextBind selectBind = new SelectTextBind(控件, data);
			selectBind.setOperateListener(new OnOperateListener() {
				
				@Override
				public List<SelectOption> getOperateList() {
					List<SelectOption> optionList = new ArrayList<>();
					optionList.add(new SelectOption(SelectOption.TYPE_SELECT_ALL, "全选"));
					optionList.add(new SelectOption(SelectOption.TYPE_COPY, "复制"));
					return optionList;
				}
				
				@Override
				public void onOperate(SelectOption operate) {
					ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(selectBind.getSelectKey());
					if (selectTextHelp != null) {
						SelectDataInfo selectDataInfo = selectTextHelp.getSelectDataInfo();
						//操作处理
						...
						
						selectTextHelp.onSelectData(null);
					}
				}
			});
			selectBind.bind();
			```
	
			* data：列表item绑定的数据，此数据不能为基础数据和字符串

		* 不含文本选择的操作

			```
			SelectBind selectBind = new SelectBind(控件, 数据);
			selectBind.setOperateListener(new OnOperateListener() {
				@Override
				public List<SelectOption> getOperateList() {
					List<SelectOption> optionList = new ArrayList<>();
					optionList.add(new SelectOption(3, "搜索"));
					optionList.add(new SelectOption(4, "分享"));
					return optionList;
				}
				
				@Override
				public void onOperate(SelectOption operate) {
					//操作处理
					...
					
					ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(selectBind.getSelectKey());
					if (selectTextHelp != null) {
						selectTextHelp.onSelectData(null);
					}
				}
			});
			selectBind.bind();
			```
	2. activity添加手势处理（解决点击空白区域关闭弹框）
	
		```
		public class ListActivity extends AppCompatActivity {
			
    		private GestureDetector gestureDetector;
    		private ListSelectTextHelp selectTextHelp;
    		
    		@Override
    		protected void onCreate(Bundle savedInstanceState) {
    			super.onCreate(savedInstanceState);
    			setContentView(R.layout.activity_list);
    			
    			...
    			
    			gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
    				
    				@Override
    				public boolean onSingleTapUp(MotionEvent e) {
    					//轻按选中会执行onLongClick和此方法，重按选中执行onLongClick
    					//点击事件执行此方法
    						
    						if (selectTextHelp != null) {
    						BaseSelectBind selectBind = selectTextHelp.getSelectBind();
    						if (selectBind != null) {
    							Log.e("详情", "onSingleTapUp-->" + selectBind);
    							if (selectBind.isTouchDown()) {
    								//说明点击到控件
    									if (selectBind.isTriggerLongClick()) {
    									//排除轻按选中数据
    										return true;
    								}
    							}
    						}
    						selectTextHelp.onSelectData(null);
    					}
    					return true;
    				}
    				
    				@Override
    				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    					Log.e("详情", "onScroll--");
    					return super.onScroll(e1, e2, distanceX, distanceY);
    				}
    				
    				@Override
    				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    					Log.e("详情", "onFling");
    					return super.onFling(e1, e2, velocityX, velocityY);
    				}
    				
    				@Override
    				public boolean onDown(MotionEvent e) {
    					Log.e("详情", "onDown");
    					if (selectTextHelp != null) {
    						BaseSelectBind selectBind = selectTextHelp.getSelectBind();
    						if (selectBind != null) {
    							selectBind.onGestureDown(e);
    						}
    					}
    					return super.onDown(e);
    				}
    			});
    			
    			listView.setOnScrollListener(new AbsListView.OnScrollListener() {
    				
    				@Override
    				public void onScrollStateChanged(AbsListView view, int scrollState) {
    					if (selectTextHelp != null) {
    						BaseSelectBind selectBind = selectTextHelp.getSelectBind();
    						if (selectBind != null) {
    							if (!selectBind.isTouchDown()) {
    								selectTextHelp.onSelectData(null);
    								return;
    							}
    							
    							if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
    								selectTextHelp.onHideSelect();
    							} else {
    								refreshPopupLocation();
    							}
    						}
    					}
    				}
    				
    				@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
						// Log.e("详情", "滚动监听--》scroll");
					}
				});
				
				ListSelectTextHelp selectTextHelp = new ListSelectTextHelp(this, this);
				SelectManager.getInstance().put(this.toString(), selectTextHelp);
				this.selectTextHelp = selectTextHelp;
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
			
			@Override
			public View getViewByData(SelectDataInfo selectDataInfo) {
				if (listView == null) {
					return null;
				}
				
				Adapter adapter = listView.getAdapter();
				if (adapter == null || adapter.getCount() == 0) {
					return null;
				}
				
				int firstVisiblePos = listView.getFirstVisiblePosition();
				int lastVisiblePos = listView.getLastVisiblePosition();
				
				int selectPos = -1;
				for (int i = 0, len = lastVisiblePos - firstVisiblePos; i <= len; i++) {
					Object itemData = adapter.getItem(i + firstVisiblePos);
					if (selectDataInfo.getObject().equals(itemData)) {
						selectPos = i;
						break;
					}
				}
				
				if (selectPos == -1) {
					//表明控件已在显示区域外
					return null;
				}
				
				View childView = listView.getChildAt(selectPos);
				//此处返回的childView需要注意adapter实现，防止获取不到指定的控件
				return childView;
			}
			
			protected void refreshPopupLocation() {
				getWindow().getDecorView().removeCallbacks(runnable);
				getWindow().getDecorView().postDelayed(runnable, 120);
			}
			
			private Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					if (selectTextHelp != null) {
						SelectDataInfo selectDataInfo = selectTextHelp.getSelectDataInfo();
						if (selectDataInfo == null) {
							return;
						}
						
						View view = selectTextHelp.getDependentView();
						if (view == null) {
							selectTextHelp.updateSelectInfo();
							return;
						}
						
						BaseSelectBind selectBind = (BaseSelectBind) view.getTag(R.id.select_bind);
						selectTextHelp.setSelectBind(selectBind);
						selectTextHelp.onShowSelect();
					}
				}
			};
			
			@Override
			protected void onDestroy() {
				super.onDestroy();
				if (selectTextHelp != null) {
					selectTextHelp.destory();
					selectTextHelp = null;
				}
				SelectManager.getInstance().clear();
			}
		}
		```
	
	
	
### 参考资料

1. [Android自定义选择复制功能](https://juejin.cn/post/6844903569682857992)
2. [参考库](https://github.com/zhouray/SelectableTextView)
