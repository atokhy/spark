/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.execution.streaming

import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.catalyst.plans.logical.LeafNode
import org.apache.spark.sql.execution.datasources.DataSource

object StreamingRelation {
  def apply(dataSource: DataSource): StreamingRelation = {
    val (name, schema) = dataSource.sourceSchema()
    StreamingRelation(dataSource, name, schema.toAttributes)
  }
}

/**
 * Used to link a streaming [[DataSource]] into a
 * [[org.apache.spark.sql.catalyst.plans.logical.LogicalPlan]]. This is only used for creating
 * a streaming [[org.apache.spark.sql.DataFrame]] from [[org.apache.spark.sql.DataFrameReader]].
 * It should be used to create [[Source]] and converted to [[StreamingExecutionRelation]] when
 * passing to [StreamExecution]] to run a query.
 */
case class StreamingRelation(dataSource: DataSource, sourceName: String, output: Seq[Attribute])
  extends LeafNode {
  override def isStreaming: Boolean = true
  override def toString: String = sourceName
}

/**
 * Used to link a streaming [[Source]] of data into a
 * [[org.apache.spark.sql.catalyst.plans.logical.LogicalPlan]].
 */
case class StreamingExecutionRelation(source: Source, output: Seq[Attribute]) extends LeafNode {
  override def isStreaming: Boolean = true
  override def toString: String = source.toString
}

object StreamingExecutionRelation {
  def apply(source: Source): StreamingExecutionRelation = {
    StreamingExecutionRelation(source, source.schema.toAttributes)
  }
}
