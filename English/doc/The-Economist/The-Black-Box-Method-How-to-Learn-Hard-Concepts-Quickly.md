---
title: 学习
code_clipboard: 
date: 2022-12-20 17:55:57
---
from [here](https://www.youtube.com/watch?v=RDzsrmMl48I)

# Introl

Hey guys this is going to be a video on essentially an effective method to learn many different concepts quickly. This method is dubbed black boxing and as you can see here I've hand-drawn a cube to represent this if you like this so this allows you to apply many different techniques like algorithems, data structures  or just pieces of code in general. And it works for competitive programming or even just general programming because there's a lot of similarity between the two and the way the method is used. 

So in particular I've used this a lot during some concests of mine to get uh where I am now with a significant example being how I use that to solve a quite hard problem with a technique. I didn't even know much about only a brief overview of what it did and that ended up getting me tenth and saving my seven star rating on code. So yeah this method works and in fact this is something we generally use all the time in general programming without even thinking much about it.

# Black Box Description/Examples

So what even is black box well we can pull up wikipedia's definition as support but essentialy it just means that it's some piece of code that does something and we understanding what it does but not really how it does it. It's sort of like they say it's viewed in terms of its inputs and outputs but not the internal mechanisms that are converting that input into the output. And the reason we don't understand what it does the reason we don't understand how it does it is because we don't need to like many apis or internal libraries like we already use for many portions of our code like input and output. For example, we let the pre-written code do the hidden magic and only worry about what we do with the results and the cool thing about programs is that you can literllay just copy and paste some piece of code in and regardless of how much you understand it, it's still going to work. 

So let's take a very common example of sorting an array for many languages. We use an internal algorithm to sort an array some part of the programming languages library and just as a brief refresher. If it's unfamiliar to you is just reordering elements in some kind of increasing order and I'm also so proud of this cube drawing that I'm going to keep it here for this example. So often in general we do learn how this algorithm works sorting or at least some viable sorting algorithm just because it's like one of the common introductory things to programming and it's often a good exercise but it's not necessary to use the library like a single function call does all the work for us. I mean all we do is we call some sorting method or function and that's it. We don't worry about what's going on inside and other library uses work the same way we simply use their functions without implementing them ourselves.

A different example is feminic trees are binary index trees which is a topic and topic from competitive programming. So if you already know these great then it's a good example and if you don't then that's fine and the whole point of this is that it will only take me like a few seconds up to a minute to explain it. 

So the quick black box description of what it is. It's basically operates on a array it supports two different operations. The first is that you can pick some index some position in the array and add some value x to it and the second is that you can query the sum of the first x elements or the first i elements both of these operations take over log n complexity and um it's O of n to initialize and oven memory overall and that's it there is some complicated **underlying bitwise** logic that makes it work.  But to use it none of that is actually neccassry to know and in fact I honestly don't even understand it myself because I've never bothered to because it's never been necessary and nonetheless I've solved many many problems using this technique. 

So my template which is the code you can see below or in front of you is sort of structured like an api where these add and query functions are just simply using the struct and telling it to do that.

# How to use it 

Now let's pull out the trusty paint for a demonstration and if you're new to this channel well I basically use it for everything. So, that will be how it goes in this diagram vertical distance represents time and basically the standard strategy for learning is to pick some list of topics and then learn them one by one. However this is rather slow and during the period of learning for the topics that you haven't even looked at yet you won't know anything or much about any of them.

