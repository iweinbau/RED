package geometry;

import core.HitRecord;
import core.Ray;
import material.Material;
import math.*;

import java.util.*;

public class BVH extends Composite{


    SplitMethod splitMethod = new EqualCountMethod();

    BVHNode root;

    public BVH(Transform3D transform) {
        super(transform);
    }

    public BVH(Transform3D transform, TriangleMesh mesh, Material material) {
        super(transform, mesh, material);
    }

    public void buildAccelerationStructure() {
        // Nothing to do here so return.
        if (this.composites.size() == 0)
            return;

        // 1. Construct information for all geometry.
        List<GeometryInfo> geometryInfo = new ArrayList<>();

        for (int i =0 ; i<composites.size(); i++) {
            geometryInfo.add(new GeometryInfo(composites.get(i).boundingBox(),i));
        }

        // 2. Construct list to order geometry for easy retrieval.
        List<Geometry> orderedGeometry = new ArrayList<>();

        // 3. Construct root element of the BVH tree
        this.root = buildRecursive(geometryInfo,orderedGeometry,0,composites.size());

        // 4. update composites with ordered geometry.
        this.composites = orderedGeometry;

        System.out.println("Finished building BVH");

    }

    BVHNode buildRecursive(List<GeometryInfo> geometryInfo, List<Geometry> orderedGeometry, int start, int end ) {
        // Construct bounding box for all geometry
        BBox bound = new BBox();
        for (int i = start; i < end; i++) {
            bound = bound.union(geometryInfo.get(i).bBox);
        }

        int numberOfGeometry = end - start;
        if (numberOfGeometry <= 1) {
            // Get offset in ordered geometry set.
            int first = orderedGeometry.size();
            // Add new geometry to the ordered set.
            for (int i = start; i < end; i++) {
                orderedGeometry.add(composites.get(geometryInfo.get(i).geometryNumber));
            }
            // Construct new leaf node.
            return new BVHNode(first,numberOfGeometry,bound);
        } else {
            // Get bounding box around centers of the geometries bounding boxes.
            BBox centeredBox = new BBox();
            for (int i = start; i<end; i++) {
                centeredBox = centeredBox.union(geometryInfo.get(i).center);
            }
            // Get spiting axis.
            int splitAxis = centeredBox.maximumExent();
            // Check if centered bounding box has a volume.
            if( centeredBox.getpMax().get(splitAxis) == centeredBox.getpMin().get(splitAxis)) {
                // Get offset in ordered geometry set.
                int first = orderedGeometry.size();
                // Add new geometry to the ordered set.
                for (int i = start; i < end; i++) {
                    orderedGeometry.add(composites.get(geometryInfo.get(i).geometryNumber));
                }
                // Construct new leaf node.
                return new BVHNode(first,numberOfGeometry,bound);
            } else {
                int splitIndex = splitMethod.split(geometryInfo,start,end,splitAxis,centeredBox);
                return new BVHNode(splitAxis,
                        buildRecursive(geometryInfo,orderedGeometry,start,splitIndex),
                        buildRecursive(geometryInfo,orderedGeometry,splitIndex,end));
            }
        }
    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        double tmin = ray.getMaxDistance();
        boolean hit = false;
        Vector3D invDir = ray.getDirection().inverse();
        boolean[] isNeg = new boolean[]{invDir.getX() < 0, invDir.getY()<0, invDir.getZ() <0};

        Stack<BVHNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            // Get next node.
            BVHNode current = stack.pop();
            hitRecord.intersectionTests++;
            // Check for an early abort fo this node.
            if(current.bBox.intersect(ray,tmin)) {
                if (current.numberGeometry > 0) {
                    // We are in a leaf node check all geometry for intersection.
                    for (int i = current.startIndex; i < current.startIndex + current.numberGeometry; i++) {
                        HitRecord tmpRecord = new HitRecord();
                        if (composites.get(i).intersect(ray, tmpRecord) && tmpRecord.getDistance() < tmin) {
                            tmin = tmpRecord.getDistance();
                            hit = true;
                            hitRecord.setIntersection(tmpRecord);
                        }
                        hitRecord.intersectionTests += tmpRecord.intersectionTests;
                    }
                }else {
                    // Add child nodes, If ray direction is negative in the split axis.
                    // We have to check first the second node so we can early abort the next one.
                    if (isNeg[current.splitAxis]) {
                        stack.push(current.leftNode);
                        stack.push(current.rightNode);
                    } else {
                        stack.push(current.rightNode);
                        stack.push(current.leftNode);
                    }
                }
            }
        }
        return hit;
    }

    private class BVHNode {
        BBox bBox;
        BVHNode leftNode;
        BVHNode rightNode;
        int splitAxis, startIndex, numberGeometry;

        public BVHNode(int first, int numberOfGeometry, BBox bound) {
            this.startIndex = first;
            this.numberGeometry = numberOfGeometry;
            this.bBox = bound;
            leftNode = null;
            rightNode = null;
            splitAxis = 0;
        }

        public BVHNode(int splitAxis, BVHNode left, BVHNode right) {
            this.bBox = right.bBox.union(left.bBox);
            this.splitAxis = splitAxis;
            this.numberGeometry = 0;
            this.leftNode = left;
            this.rightNode = right;
        }
    }

    class GeometryInfo {
        Point3D center;
        BBox bBox;
        int geometryNumber;

        public GeometryInfo(BBox bBox, int geometryNumber) {
            this.bBox = bBox;
            this.geometryNumber = geometryNumber;
            this.center = bBox.center();
        }
    }

    public abstract class SplitMethod {
        abstract int split(List<BVH.GeometryInfo> geometryInfo, int start, int end, int splitAxis, BBox centered);
    }

    public class MidPointMethod extends SplitMethod {

        @Override
        int split(List<BVH.GeometryInfo> geometryInfo, int start, int end, int splitAxis, BBox centered) {
            double centerValue = centered.center().get(splitAxis);
            if (start == end) return start;

            int first = findFirst(geometryInfo, start, end, splitAxis, centerValue);
            for (int i = first+1; i < end; i++) {
                if (!shouldSwap(geometryInfo.get(i),splitAxis,centerValue)) {
                    Collections.swap(geometryInfo,i,first);
                    first++;
                }
            }
            // this will be a bad split so do EqualCount for this case;
            if( first == start || first == end)
                return new EqualCountMethod().split(geometryInfo,start,end,splitAxis,centered);
            return first;
        }

        private int findFirst(List<BVH.GeometryInfo> geometryInfo, int start, int end, int splitAxis, double value) {
            for (int i = start; i < end; ++i) {
                if(shouldSwap(geometryInfo.get(i),splitAxis,value)) {
                    return i;
                }
            }
            return end;
        }

        boolean shouldSwap(BVH.GeometryInfo geometryInfo, int splitAxis, double value) {
            return geometryInfo.center.get(splitAxis) > value;
        }

    }

    public class EqualCountMethod extends SplitMethod{

        @Override
        int split(List<BVH.GeometryInfo> geometryInfo, int start, int end, int splitAxis, BBox centered) {
            if (start == end)
                return start;
            geometryInfo.subList(start,end).sort((BVH.GeometryInfo i1, BVH.GeometryInfo i2) ->
                    (i1.center.get(splitAxis).compareTo(i2.center.get(splitAxis))));
            return ((start + end) / 2);
        }
    }
}

