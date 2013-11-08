package cn.com.qimingx.dbe.action.bean;

/**
 * @author inc062805
 * 
 * Ext Tree Loader 的参数Bean
 */
public class GridTableLoadBean extends TreeNodeBean {
	private int start = 0;
	private int limit = 25;
	private String search;
	private String searchValue;

	public String getSearchCondition() {
		String cond = "";
		if (getSearch() != null && getSearchValue() != null) {
			if (getSearch().length() > 0 && getSearchValue().length() > 0) {
				String value = getSearchValue().toLowerCase();
				value = "'%" + value + "%'";
				String[] searchs = getSearch().split(",");
				for (String search : searchs) {
					if (cond.length() > 0) {
						cond += " or ";
					}
					cond += "lower(" + search + ") like " + value;
				}
			}
		}
		return cond;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
}
