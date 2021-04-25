datatype tree = None
                |Node of tree*int*tree;

fun compare(x:int, y:int):order=
    if x<y then LESS else
    if y<x then GREATER else EQUAL;
fun treeCompare(Node(L1,v1,R1),Node(L2,v2,R2)) = compare(v1,v2)
    | treeCompare(Node(L,v,R),None) = LESS
    | treeCompare(None,Node(L,v,R)) = GREATER
    | treeCompare(None,None) = EQUAL;

fun listToTree [] = None
    |listToTree (x::L) = let
                            val index = List.length L div 2
                            val ltree = List.take(L,index)
                            val rtree = List.drop(L,index)
                          in
                            Node(listToTree(ltree),x,listToTree(rtree))
                          end;



fun SwapDown None = None
| SwapDown(Node(L,v,R)) = case treeCompare(L,R) of
		LESS => (case treeCompare(L,Node(L,v,R)) of
					LESS => let val Node(l1,l,r1) = L
                            in Node(SwapDown(Node(l1,v,r1)),l,R)
                            end
					|GREATER => Node(L,v,R)
                    |EQUAL => Node(L,v,R))
		|EQUAL => (case treeCompare(R,Node(L,v,R)) of
					GREATER=> Node(L,v,R)
                    |EQUAL =>  let val Node(l1,r,r1) = R
                            in Node(L,r,SwapDown(Node(l1,v,r1)))
                            end
					|LESS =>  let val Node(l1,r,r1) = R
                            in Node(L,r,SwapDown(Node(l1,v,r1)))
                            end)
		|GREATER => (case treeCompare(R,Node(L,v,R)) of
					GREATER=> Node(L,v,R)
                    |EQUAL =>  let val Node(l1,r,r1) = R
                            in Node(L,r,SwapDown(Node(l1,v,r1)))
                            end
					|LESS =>  let val Node(l1,r,r1) = R
                            in Node(L,r,SwapDown(Node(l1,v,r1)))
                            end);

fun heapify None = None
    | heapify(Node(L,v,R)) = SwapDown(Node(heapify(L),v,heapify(R)))
val t1 = listToTree([2,1,3])
val t2 = heapify(t1);
