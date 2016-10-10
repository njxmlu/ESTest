package ESTest.bean;

/**
 * Created by xiaosi on 16-9-29.
 *
 * 大搜 --- 点击条目或者展示条目
 *
 */
public class SuggestionItem {

    // 条目名称
    private String itemName;
    // 类型 ticket hotel flight vacation
    private String businessType;
    // 提示
    private String hint;
    // 价格
    private double price;
    // 点击或者展现次数
    private int itemCount;
    // 类型占比
    private double typeRatio;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTypeRatio() {
        return typeRatio;
    }

    public void setTypeRatio(double typeRatio) {
        this.typeRatio = typeRatio;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "SuggestionItem{" +
                "itemName='" + itemName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", hint='" + hint + '\'' +
                ", price=" + price +
                ", typeRatio=" + typeRatio +
                '}';
    }

}
