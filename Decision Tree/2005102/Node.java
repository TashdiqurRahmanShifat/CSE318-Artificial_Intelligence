import java.util.HashMap;

class Node {
    String className;
    String attributeName;
    boolean isLeaf;
    HashMap<String,Node>childNodeList;
    public Node(String name,boolean isLeaf)
    {
        if(isLeaf)
        {
            className=name;//label name of leaf node
        }
        else
        {
            attributeName=name;
            childNodeList=new HashMap<>();
        }

        this.isLeaf=isLeaf;
    }
    public void addChildNode(String attrVal,Node childNode)
    {
        childNodeList.put(attrVal,childNode);
    }
}
