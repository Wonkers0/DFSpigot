### This guide will walk you through the internals of DF Spigotifier so that you can start working on it yourself, just as if it were your own code!

# Table Of Contents:

1. [DFValue.java](#dfvalue---the-storing-of-diamondfire-values-in-java-objects)

2. [DFVar.java](#dfvar---storing-information-about-variables)

3. [ParamManager.java](#parammanager---crafting-a-good-developer-experience-for-writing-action-logic)

## DFValue - *The storing of DiamondFire values in Java Objects*
One of the main things that need to be processed before action logic can be written are the values inside the chests. These values are stored in a class called `DFValue.java`, which holds a value, a slot (the chest slot that the value is placed in), and a type.

```java
private Object val;
public Integer slot = null;
public DFType type;

public DFValue(Object val, Integer slot, DFType type){
  this.val = sanitizeInput(val, type);
  this.slot = slot;
  this.type = type;
}

public DFValue(Object val, DFType type){
  this.val = sanitizeInput(val, type);
  this.type = type;
}
```

As you can see, there are 2 constructors. In certain cases, such as when we store DFValues in variables, there won't be a slot to store, so in that case we'd use the 2nd constructor to create our instance.

When you create an instance of the `DFValue.java` class, the "value" field that you pass to the constructor is briefly processed in the `sanitizeInput` function, which takes a value and its type. 👇

```java
@SuppressWarnings({"rawtypes"})
private Object sanitizeInput(Object val, DFType type){
  if(val instanceof ArrayList){
    ArrayList<DFValue> result = new ArrayList<>();
    for(Object item : (ArrayList<Object>) val){
      if(item == null){
        result.add(new DFValue(null, DFType.ANY));
        continue;
      }

      result.add(new DFValue(item, type));
    }

    return result.toArray(DFValue[]::new);
  }
  else if(val instanceof Integer) return (double) (int) val;

  return val;
}
```

This isn't extremely complicated, it just converts any `ArrayList` values to a `DFValue` and converts integer values to doubles.

---

Next up, you have to provide a type for your value; As in, the `DFType`, which is an enum. The possible values are as follows:  
```java
public enum DFType {
	TXT,
	NUM,
	SND,
	LOC,
	ITEM,
	POT,
	VAR,
	LIST,
	VEC,
	PART,
	GAMEVAL, // This is only used internally for retrieving certain game values
	ANY // This is only used internally for parameter types.
}
```

So, if you had a number, you'd pass `DFType.NUM`, or if you had a variable, you'd pass `DFType.VAR`. And yes, `DFValues` can *act* as variables, but they don't store the value of the variable, they store an instance of the `DFVar.java` class, which we'll go into more detail in a bit.

The only other non-static methods in this class are pretty self-explanatory, but I'd just like to briefly explain the `getVal` function and how it processes a value before returning it.
```java
public Object getVal(){
  switch(type){
    case LOC:
      Location l = (Location) val;
      Location o = DFPlugin.origin;
      return l == null ? null : new Location(l.getWorld(), l.getX() + o.getX(), l.getY(), l.getZ() + o.getZ(), l.getYaw(), l.getPitch());
    default:
      return val;
  }
}

```

So far, only locations will be processed. In DiamondFire, locations are relative to the North-West corner of your plot, but on your server you can pick your point of origin using `/dfspigot set origin` (by default [0,0,0]). All this function does is that it adds the origin to any location values, to make them absolute instead of relative. 


## DFVar - *Storing information about variables*
It may be a common misconception that instances of the DFVar class store the __value__ of variables, but those values are actually stored in a hashmap instead (1 hashmap per variable scope).

```java
public Scope scope;
public String name;

public DFVar(String name, Scope scope){ // Constructor for making an instance of this class. Won't do anything by itself.
  this.name = name;
  this.scope = scope;
}
```

Looking at the constructor of this class, there's only 2 fields: The variable name and its scope.

`DFVar.java` is simply storing these 2 pieces of information about a given variable. You can use this information, however, to retrieve the value that a variable is currently storing. Let's look at the static `getVar` method:

```java
// Use this method to get the value of a variable
public static DFValue getVar(DFVar var, HashMap<String, DFValue> localStorage){
  return varExists(var, localStorage) ? determineStorage(var.scope, localStorage).get(var.name) : DFValue.nullVar();
}
```

This code reads somewhat like english. First, we check if the variable exists. If it does, we'll find the hashmap that it's stored in and retrieve it's value using its name. If it doesn't, we'll return a "null" variable instead, which is this constant:

```java
public static DFValue nullVar(){
  return new DFValue(0, DFType.NUM);
}
```

These are the hashmaps for global and saved variables:
```java
	public static HashMap<String, DFValue> globalVars = new HashMap<>();
	public static HashMap<String, DFValue> savedVars = new HashMap<>();
  /*
  Variables are mostly just DFValue instances tied to a String (their name). Instances of this class will only be used
  when formatting arguments to mimic the in-game variable data structure.
   */
```

Saved variables are actually saved in a file, and when the plugin is initialized they are deserialized into a hashmap. Local variables are in a hashmap of their own, often referred to as `localStorage` in the code. There is one hashmap per event, and you'll often need to pass this `localStorage` hashmap to other code if you want to do things involving variables.

## ParamManager - *Crafting a good developer experience for writing action logic*
TODO
