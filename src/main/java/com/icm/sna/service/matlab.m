function s=path(a)
    aa=[];
    siz=size(a);
    siz=siz(1);
    for k = 1:siz
        aa=[aa;graphshortestpath(sparse(a),k)];
    end
    s=sum(sum(aa))/(siz*(siz-1));
end