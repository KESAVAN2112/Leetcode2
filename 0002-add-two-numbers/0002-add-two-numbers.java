class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode add = new ListNode(0);
        ListNode curr =add;
        int carry = 0;
        while(l1!=null||l2!=null||carry!=0)
        {
            int d1=(l1!=null)?l1.val:0;
            int d2=(l2!=null)?l2.val:0;
            int sum=d1+d2+carry;
            int node= sum%10;
            carry=sum/10;
            ListNode newNode = new ListNode(node);
            curr.next=newNode;
            curr=curr.next;
            l1=(l1!=null)?l1.next:null;
            l2=(l2!=null)?l2.next:null;
        }
        ListNode result = add.next;
        add.next=null;
        return result;        
    }
}