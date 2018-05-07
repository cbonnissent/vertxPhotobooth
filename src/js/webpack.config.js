const path = require('path')

module.exports = {
	module: {
		rules: [
			{
				test: /\.js$/,
				exclude: /node_modules/,
				loader: 'babel-loader',

				options: {
					presets: ['env']
				}
			},
			{
				test: /\.(scss|css)$/,

				use: [
					{
						loader: 'style-loader'
					},
					{
						loader: 'css-loader'
					},
					{
						loader: 'sass-loader'
					}
				]
			}
		]
	},

	entry: {
		app: './app.js'
	},

	output: {
		filename: '[name].js',
		path: path.resolve(__dirname, '../main/resources/webroot/dist/')
	},

	mode: 'development'
};
