package bow.util

import java.util.*

/*
** KDTree.java by Julian Kent
** Licenced under the  Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
** See full licencing details here: http://creativecommons.org/licenses/by-nc-sa/3.0/
**
** For additional licencing rights please contact jkflying@gmail.com
**
*/

abstract class KDTree<T>(private val _dimensions: Int) {

    private var _nodes: Int = 0

    private val root: Node

    private val nodeList = ArrayList<Node>()

    // prevent GC from having to collect _bucketSize*dimensions*8 bytes each
    // time a leaf splits
    private var mem_recycle: DoubleArray? = null

    // the starting values for bounding boxes, for easy access
    private val bounds_template: DoubleArray

    // one big self-expanding array to keep all the node bounding boxes so that
    // they stay in cache
    // node bounds available at:
    // low: 2 * _dimensions * node.index + 2 * dim
    // high: 2 * _dimensions * node.index + 2 * dim + 1
    private val nodeMinMaxBounds: ContiguousDoubleArrayList

    init {

        // initialise this big so that it ends up in 'old' memory
        nodeMinMaxBounds = ContiguousDoubleArrayList(512 * 1024 / 8 + 2 * _dimensions)
        mem_recycle = DoubleArray(_bucketSize * _dimensions)

        bounds_template = DoubleArray(2 * _dimensions)
        Arrays.fill(bounds_template, java.lang.Double.NEGATIVE_INFINITY)
        var i = 0
        val max = 2 * _dimensions
        while (i < max) {
            bounds_template[i] = java.lang.Double.POSITIVE_INFINITY
            i += 2
        }

        // and.... start!
        root = Node()
    }

    fun nodes(): Int {
        return _nodes
    }

    fun size(): Int {
        return root.entries
    }

    fun addPoint(location: DoubleArray, payload: T): Int {

        var addNode = root
        // Do a Depth First Search to find the Node where 'location' should be
        // stored
        while (addNode.pointLocations == null) {
            addNode.expandBounds(location)
            if (location[addNode.splitDim] < addNode.splitVal)
                addNode = nodeList[addNode.lessIndex]
            else
                addNode = nodeList[addNode.moreIndex]
        }
        addNode.expandBounds(location)

        val nodeSize = addNode.add(location, payload)

        if (nodeSize % _bucketSize == 0)
        // try splitting again once every time the node passes a _bucketSize
        // multiple
            addNode.split()

        return root.entries
    }

    fun nearestNeighbours(
        searchLocation: DoubleArray, K: Int
    ): ArrayList<SearchResult<T>> {
        val stack = IntStack()
        val results = PrioQueue<T>(K, true)

        stack.push(root.index)

        var added = 0

        while (stack.size() > 0) {
            val nodeIndex = stack.pop()
            if (added < K || results.peekPrio() > pointRectDist(
                    nodeIndex,
                    searchLocation
                )
            )
                added += nodeList[nodeIndex].search(
                    searchLocation, stack,
                    results
                )
        }

        val returnResults = ArrayList<SearchResult<T>>(
            K
        )
        val priorities = results.priorities
        val elements = results.elements
        for (i in 0 until K) {// forward (closest first)
            val s = SearchResult(
                priorities[i],
                elements?.get(i) as T
            )
            returnResults.add(s)
        }
        return returnResults
    }

    internal abstract fun pointRectDist(offset: Int, location: DoubleArray): Double

    internal abstract fun pointDist(arr: DoubleArray, location: DoubleArray, index: Int): Double

    open class Euclidean<T>(dims: Int) : KDTree<T>(dims) {


        override fun pointRectDist(offset: Int, location: DoubleArray): Double {
            var offset = offset
            offset *= 2 * super._dimensions
            var distance = 0.0
            val array = super.nodeMinMaxBounds.array
            var i = 0
            while (i < location.size) {

                var diff = 0.0
                var bv = array[offset]
                val lv = location[i]
                if (bv > lv)
                    diff = bv - lv
                else {
                    bv = array[offset + 1]
                    if (lv > bv)
                        diff = lv - bv
                }
                distance += sqr(diff)
                i++
                offset += 2
            }
            return distance
        }

        override fun pointDist(arr: DoubleArray, location: DoubleArray, index: Int): Double {
            // final double[] arr = searchNode.pointLocations.array;
            var distance = 0.0
            var offset = (index + 1) * super._dimensions

            var i = super._dimensions
            while (i-- > 0) {
                distance += sqr(arr[--offset] - location[i])
            }
            return distance
        }

    }

    class Manhattan<T>(dims: Int) : KDTree<T>(dims) {

        override fun pointRectDist(offset: Int, location: DoubleArray): Double {
            var offset = offset
            offset *= 2 * super._dimensions
            var distance = 0.0
            val array = super.nodeMinMaxBounds.array
            var i = 0
            while (i < location.size) {

                var diff = 0.0
                var bv = array[offset]
                val lv = location[i]
                if (bv > lv)
                    diff = bv - lv
                else {
                    bv = array[offset + 1]
                    if (lv > bv)
                        diff = lv - bv
                }
                distance += diff
                i++
                offset += 2
            }
            return distance
        }

        override fun pointDist(arr: DoubleArray, location: DoubleArray, index: Int): Double {
            // final double[] arr = searchNode.pointLocations.array;
            var distance = 0.0
            var offset = (index + 1) * super._dimensions

            var i = super._dimensions
            while (i-- > 0) {
                distance += Math.abs(arr[--offset] - location[i])
            }
            return distance
        }

    }

    // NB! This Priority Queue keeps things with the LOWEST priority.
    // If you want highest priority items kept, negate your values
    private class PrioQueue<S> internal constructor(size: Int, prefill: Boolean) {

        internal var elements: Array<Any> ?= null

        internal var priorities: DoubleArray

        private var minPrio: Double = 0.toDouble()

        private var size: Int = 0

        init {
            //elements = arrayOfNulls(size)
            priorities = DoubleArray(size)
            Arrays.fill(priorities, java.lang.Double.POSITIVE_INFINITY)
            if (prefill) {
                minPrio = java.lang.Double.POSITIVE_INFINITY
                this.size = size
            }
        }

        // uses O(log(n)) comparisons and one big shift of size O(N)
        // and is MUCH simpler than a heap --> faster on small sets, faster JIT

        internal fun addNoGrow(value: S, priority: Double) {
            val index = searchFor(priority)
            val nextIndex = index + 1
            val length = size - index - 1// remove dependancy on nextIndex
            System.arraycopy(elements, index, elements, nextIndex, length)
            System.arraycopy(priorities, index, priorities, nextIndex, length)
            elements?.set(index, value!!)
            priorities[index] = priority

            minPrio = priorities[size - 1]
        }

        internal fun searchFor(priority: Double): Int {
            var i = size - 1
            var j = 0
            while (i >= j) {
                val index = (i + j).ushr(1)

                if (priorities[index] < priority)
                    j = index + 1
                else
                    i = index - 1
            }
            return j
        }

        internal fun peekPrio(): Double {
            return minPrio
        }
    }

    class SearchResult<S> internal constructor(var distance: Double, var payload: S)

    private inner class Node @JvmOverloads internal constructor(pointMemory: DoubleArray = DoubleArray(_bucketSize * _dimensions)) {

        // for accessing bounding box data
        // - if trees weren't so unbalanced might be better to use an implicit
        // heap?
        internal var index: Int = 0

        // keep track of size of subtree
        internal var entries: Int = 0

        // leaf
        internal var pointLocations: ContiguousDoubleArrayList? = null

        internal var pointPayloads: ArrayList<T>? = ArrayList(_bucketSize)

        // stem
        // Node less, more;
        internal var lessIndex: Int = 0
        internal var moreIndex: Int = 0

        internal var splitDim: Int = 0

        internal var splitVal: Double = 0.toDouble()

        init {
            pointLocations = ContiguousDoubleArrayList(pointMemory)
            index = _nodes++
            nodeList.add(this)
            nodeMinMaxBounds.add(bounds_template)
        }

        // returns number of points added to results
        internal fun search(searchLocation: DoubleArray, stack: IntStack, results: PrioQueue<T>): Int {
            if (pointLocations == null) {

                if (searchLocation[splitDim] < splitVal)
                    stack.push(moreIndex).push(lessIndex)// less will be popped
                else
                    stack.push(lessIndex).push(moreIndex)// first
                // more will be popped
                // first

            } else {
                var updated = 0
                var j = entries
                while (j-- > 0) {
                    val distance = pointDist(
                        pointLocations!!.array,
                        searchLocation, j
                    )
                    if (results.peekPrio() > distance) {
                        updated++
                        results.addNoGrow(pointPayloads!![j], distance)
                    }
                }
                return updated
            }
            return 0
        }

        internal fun expandBounds(location: DoubleArray) {
            entries++
            var mio = index * 2 * _dimensions
            for (i in 0 until _dimensions) {
                nodeMinMaxBounds.array[mio] = Math.min(
                    nodeMinMaxBounds.array[mio++], location[i]
                )
                nodeMinMaxBounds.array[mio] = Math.max(
                    nodeMinMaxBounds.array[mio++], location[i]
                )
            }
        }

        internal fun add(location: DoubleArray, load: T): Int {
            pointLocations!!.add(location)
            pointPayloads!!.add(load)
            return entries
        }

        internal fun split() {
            var offset = index * 2 * _dimensions

            var diff = 0.0
            for (i in 0 until _dimensions) {
                val min = nodeMinMaxBounds.array[offset]
                val max = nodeMinMaxBounds.array[offset + 1]
                if (max - min > diff) {
                    var mean = 0.0
                    for (j in 0 until entries)
                        mean += pointLocations!!.array[i + _dimensions * j]

                    mean = mean / entries
                    var varianceSum = 0.0

                    for (j in 0 until entries)
                        varianceSum += sqr(mean - pointLocations!!.array[i + _dimensions * j])

                    if (varianceSum > diff * entries) {
                        diff = varianceSum / entries
                        splitVal = mean

                        splitDim = i
                    }
                }
                offset += 2
            }

            // kill all the nasties
            if (splitVal == java.lang.Double.POSITIVE_INFINITY)
                splitVal = java.lang.Double.MAX_VALUE
            else if (splitVal == java.lang.Double.NEGATIVE_INFINITY)
                splitVal = java.lang.Double.MIN_VALUE
            else if (splitVal == nodeMinMaxBounds.array[index * 2 * _dimensions
                        + 2 * splitDim + 1]
            )
                splitVal = nodeMinMaxBounds.array[index * 2 * _dimensions + 2 * splitDim]

            val less = mem_recycle?.let { Node(it) }// recycle that memory!
            val more = Node()
            lessIndex = less!!.index
            moreIndex = more.index

            // reduce garbage by factor of _bucketSize by recycling this array
            val pointLocation = DoubleArray(_dimensions)
            for (i in 0 until entries) {
                System.arraycopy(
                    pointLocations!!.array, i * _dimensions,
                    pointLocation, 0, _dimensions
                )
                val load = pointPayloads!![i]

                if (pointLocation[splitDim] < splitVal) {
                    less.expandBounds(pointLocation)
                    less.add(pointLocation, load)
                } else {
                    more.expandBounds(pointLocation)
                    more.add(pointLocation, load)
                }
            }
            if (less.entries * more.entries == 0) {
                // one of them was 0, so the split was worthless. throw it away.
                _nodes -= 2// recall that bounds memory
                nodeList.removeAt(moreIndex)
                nodeList.removeAt(lessIndex)
            } else {

                // we won't be needing that now, so keep it for the next split
                // to reduce garbage
                mem_recycle = pointLocations!!.array

                pointLocations = null

                pointPayloads!!.clear()
                pointPayloads = null
            }
        }

    }

    private class ContiguousDoubleArrayList internal constructor(internal var array: DoubleArray) {

        internal var size: Int = 0

        internal constructor(size: Int) : this(DoubleArray(size)) {}

        internal fun add(da: DoubleArray): ContiguousDoubleArrayList {
            if (size + da.size > array.size)
                array = Arrays.copyOf(array, (array.size + da.size) * 2)

            System.arraycopy(da, 0, array, size, da.size)
            size += da.size
            return this
        }
    }

    private class IntStack internal constructor(internal var array: IntArray) {

        internal var size: Int = 0

        @JvmOverloads
        internal constructor(size: Int = 64) : this(IntArray(size)) {
        }

        internal fun push(i: Int): IntStack {
            if (size >= array.size)
                array = Arrays.copyOf(array, (array.size + 1) * 2)

            array[size++] = i
            return this
        }

        internal fun pop(): Int {
            return array[--size]
        }

        internal fun size(): Int {
            return size
        }
    }

    companion object {

        // use a big bucketSize so that we have less node bounds (for more cache
        // hits) and better splits
        private val _bucketSize = 50

        internal fun sqr(d: Double): Double {
            return d * d
        }
    }

}