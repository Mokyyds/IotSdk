package com.iotsdk.db;

public class IotTab {


		private String tabName;
		private String columns;
		private String[] indexs;
		
		/**
		 * 
		 * @param tabName 所建表的表名
		 * @param columns	所建表的字段，"(TagId INTEGER PRIMARY KEY, Name TEXT)"
		 * @param indexs 需要建立索引的字段
		 */
		public IotTab(String tabName, String columns, String[] indexs) {
			this.tabName = tabName;
			this.columns = columns;
			this.indexs = indexs;
		}

		public String getTabName() {
			return tabName;
		}

		public String getColumns() {
			return columns;
		}

		public String[] getIndexs() {
			return indexs;
		}
	
}
