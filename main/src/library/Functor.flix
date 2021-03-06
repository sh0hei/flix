/*
 *  Copyright 2020 Matthew Lutze
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

///
/// A type class for types that can be mapped over.
///
class Functor[m :# Type -> Type] {
    ///
    /// Maps the function `f` over the contents of `x`.
    ///
    pub def map(f: a -> b, x: m[a]): m[b]
}

instance Functor[Option] {
    def map(f: a -> b, x: Option[a]): Option[b] = Option.map(f, x)
}

instance Functor[List] {
    def map(f: a -> b, x: List[a]): List[b] = List.map(f, x)
}

// TODO: move instances and tests to proper namespaces
