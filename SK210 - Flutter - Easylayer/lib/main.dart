import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}
class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('samples.flutter.dev/gedi');

  Future<void> _impressao() async {
    try {
      await platform.invokeMethod('imprimir');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  Future<void> _leitorscann() async {
    try {
      await platform.invokeMethod('leitorscann');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  Future<void> _tef() async {
    try {
      await platform.invokeMethod('tef');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }


  var newTaskCtrl = TextEditingController();
  var listaNomeFuncoes = [
    {"name": "Impressão", "img": "assets/print.png"},
    {"name": "Leitor Scanner", "img": "assets/barcode.png"},
    {"name": "Tef", "img": "assets/pos-terminal.png"},
  ];

  void trocarTela(int id) {
    switch (id) {
      case 0:
        _impressao(); // Adicione os parênteses para chamar a função
        break;

      case 1:
        _leitorscann(); // Adicione os parênteses para chamar a função
        break;

      case 2:
        _tef(); // Adicione os parênteses para chamar a função
        break;
    }
  }


  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(
      context,
      designSize: Size(360, 690),
    );

    return Scaffold(
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          Container(
            padding: EdgeInsets.only(top: 60),
            child: Column(
              children: <Widget>[
                Image.asset('assets/gertec.png'),
                Text(
                  "Exemplo SK210 - Flutter 1.0.0",
                  style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 20.sp,
                      color: Colors.black87),
                ),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              shrinkWrap: true,
              itemCount: listaNomeFuncoes.length,
              itemExtent: 80,
              scrollDirection: Axis.vertical,
              itemBuilder: (BuildContext context, int index) {
                return Container(
                  decoration: const BoxDecoration(
                    border: Border(bottom: BorderSide(color: Colors.black12)),
                  ),
                  child: ListTile(
                    dense: true,
                    leading: Image(
                      image: AssetImage(listaNomeFuncoes[index]["img"]!),
                    ),
                    onTap: () {
                      trocarTela(index);
                    },
                    title: Center(
                      child: Text(
                        listaNomeFuncoes[index]["name"]!,
                        style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 20.sp,
                            color: Colors.black54),
                      ),
                    ),
                  ),
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
